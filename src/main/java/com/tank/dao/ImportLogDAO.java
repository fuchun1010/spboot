package com.tank.dao;

import com.mashape.unirest.http.Unirest;
import com.tank.common.JacksonObjectMapper;
import com.tank.domain.ImportedUnit;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static java.util.logging.Level.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author fuchun
 */
@Service
public class ImportLogDAO {

  public void updateTotalRecordsByRecordFlag(ImportedUnit importedUnit) {
    Object[] params = {importedUnit.getTotalRows(), importedUnit.getUuid()};
    int updateStatus = this.oracleJdbcTemplate.update("update FSAMPLE_IMPORTING_LOGS set total_records = ? where record_flag = ?", params);
    System.out.println(updateStatus);

  }


  /**
   * 导入结束更改日志状态
   *
   * @param importedUnit
   */
  public void endImportedLog(ImportedUnit importedUnit) {
    Unirest.setObjectMapper(new JacksonObjectMapper());
    val table = importedUnit.getTableName();
    val uuid = importedUnit.getUuid();
    val creator_email = importedUnit.getCreator_email();
    val end_time = new Date().getTime();
    val imported_status = "success";
    val counterSql = "select count(*) as cnt from " + table + " where recordFlag = ?";
    val parameters = new Object[]{importedUnit.getUuid()};
    Integer importedNum = this.oracleJdbcTemplate.queryForObject(counterSql, parameters, (rs, rowNum) -> rs.getInt("cnt"));
    //TODO
    val sb = this.importLogMessage(creator_email);
    try {
        val sql = "update FSAMPLE_IMPORTING_LOGS SET imported_status = ?,end_ts = ? where record_flag = ?";
        Object[] params = new Object[]{imported_status,end_time,uuid};
        val statusSql =  this.oracleJdbcTemplate.update(sql,params);

        if (statusSql == 1) {
           sb.append(" success imported ");
           sb.append(importedNum);
           sb.append(" records");
           logger.log(INFO, sb.toString());
      }

    } catch (Exception e) {
      sb.append(" end imported exception:---->");
      sb.append(e.getLocalizedMessage());
      logger.log(WARNING, sb.toString());
      e.printStackTrace();
    }
  }

  public void importFailed(ImportedUnit importedUnit, String failure_reason) {
    val uuid = importedUnit.getUuid();
    Object[] params = new Object[]{uuid,failure_reason};

    val sql = "insert into fsample_importing_failure(record_flag, reason) values(?, ?)";
    try {
      this.oracleJdbcTemplate.update(sql, params);
    }catch(DataAccessException e) {
      e.printStackTrace();
      logger.log(WARNING, e.getLocalizedMessage());
    }
  }



  /**
   * 开始一个excel导入日志
   *
   * @param importedUnit
   */
  //TODO 创建写入日志不需要再调用nodejs了，直接写入到oracle
  public void startImportLog(ImportedUnit importedUnit) {
    //TODO
    Unirest.setObjectMapper(new JacksonObjectMapper());
    val tableName = importedUnit.getTableName();
    val desc = importedUnit.getDesc();
    val creator_email = importedUnit.getCreator_email();
    val uploader_email = importedUnit.getUploader_email();
    val imported_time = System.currentTimeMillis();
    val uuid = importedUnit.getUuid();
    val imported_status = "appending";
    val imported_desc = importedUnit.getImported_desc();
    val visible = 1;

    val sql = "insert into FSAMPLE_IMPORTING_LOGS(imported_table_name,table_desc,creator_email,imported_by_email,imported_time," +
              "record_flag,imported_status,imported_desc,visible) " +
              "values ( ? , ? , ? , ? , ? , ? , ? , ? , ?) ";
    try {
      Object[] params = new Object[]{
              tableName,desc,creator_email,
              uploader_email,imported_time,uuid,
              imported_status,imported_desc,visible};
      this.oracleJdbcTemplate.update(sql,params);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  /**
   * 根据 RECORDFLAG 查询出上传成功的条数
   * @param importedUnit
   */
  public void importedSuccessRcordsNumber(ImportedUnit importedUnit) {
    val tableName = importedUnit.getTableName();
    val recordflag = importedUnit.getUuid();
    val sql = " select count(*) as totalRows from " + tableName + " where recordflag = ? ";
    Object[] params = {recordflag};
    int importedNum = this.oracleJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> rs.getInt("totalRows"));
    System.out.println("成功:"+ importedNum);
    importedUnit.setSuccess_records(importedNum);   //把获取的总数传给 success_records
  }

  /**
   * 得到上传成功总条数，根据recordFlag ，更改数据库上传成功的总条数，
   * @param importedUnit
   */
  public void updateSuccessRecords(ImportedUnit importedUnit){
    val success_records = importedUnit.getSuccess_records();
    val record_flag = importedUnit.getUuid();
    Object[] param = {success_records, record_flag};
    int updateStatus = this.oracleJdbcTemplate.update("update FSAMPLE_IMPORTING_LOGS set success_records = ? where record_flag = ?", param);
    System.out.println(updateStatus);//成功打印 1
  }


  /**
   * 按uuid删除一个已经导入的数据块
   *
   * @param uuid
   */
  public void delImportedData(@NonNull final String tableName, @NonNull final String uuid) {
    val sql = "delete from " + tableName + " where recordFlag =? ";
    Object[] params = {uuid};
    this.oracleJdbcTemplate.update(sql, params);
  }

  private StringBuilder importLogMessage(@NonNull String creator_id) {
    val sb = new StringBuilder();
    sb.append("creator id :");
    sb.append(creator_id);
    return sb;
  }

  @Autowired
  private JdbcTemplate oracleJdbcTemplate;

  @Value("${esAgent.endImportLogUrl}")
  private String endImportLogUrl;

  @Value("${esAgent.startImportLog}")
  private String startImportLog;

  private Logger logger = Logger.getLogger(ImportLogDAO.class.getName());
}
