package com.tank.dao;

import com.mashape.unirest.http.HttpClientHelper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.tank.common.JacksonObjectMapper;
import com.tank.domain.ImportedUnit;
import com.tank.message.status.StatusRes;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static java.util.logging.Level.*;

import java.util.logging.Logger;

/**
 * @author fuchun
 */
@Service
public class ImportLogDAO {


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

    val counterSql = "select count(*) as cnt from " + table + " where recordFlag = ?";
    val parameters = new Object[]{importedUnit.getUuid()};
    Integer importedNum = this.oracleJdbcTemplate.queryForObject(counterSql, parameters, (rs, rowNum) -> rs.getInt("cnt"));
    val request = Unirest.post(endImportLogUrl)
        .field("uuid", uuid)
        .field("count", importedNum)
        .getHttpRequest();
    val sb = this.importLogMessage(creator_email);
    try {
      val statusRes = HttpClientHelper.request(request, StatusRes.class).getBody();

      if (statusRes.isSuccess()) {
        sb.append(" success imported ");
        sb.append(importedNum);
        sb.append(" records");
        logger.log(INFO, sb.toString());
      }

    } catch (UnirestException e) {
      sb.append(" end imported exception:---->");
      sb.append(e.getLocalizedMessage());
      logger.log(WARNING, sb.toString());
      e.printStackTrace();
    }

  }



  /**
   * 开始一个excel导入日志
   *
   * @param importedUnit
   */
  public void startImportLog(ImportedUnit importedUnit) {
    Unirest.setObjectMapper(new JacksonObjectMapper());
    val table = importedUnit.getTableName();
    val uuid = importedUnit.getUuid();
    val creator_email = importedUnit.getCreator_email();
    val desc = importedUnit.getDesc();
    val imported_desc = importedUnit.getImported_desc();
    val request = Unirest.post(startImportLog)
        .field("table", table)
        .field("uuid", uuid)
        .field("desc", desc)
        .field("creator_email", creator_email)
        .field("imported_desc", imported_desc).getHttpRequest();
    val sb = this.importLogMessage(creator_email);
    try {
      val status = HttpClientHelper.request(request, StatusRes.class).getBody();
      if (status.isSuccess()) {
        sb.append(" success start import data");
        logger.log(INFO, sb.toString());
      }
    } catch (UnirestException e) {
      sb.append(" start import exception:---->");
      sb.append(e.getLocalizedMessage());
      logger.log(WARNING, sb.toString());
      e.printStackTrace();
    }

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
