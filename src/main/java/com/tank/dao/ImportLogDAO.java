package com.tank.dao;

import com.mashape.unirest.http.HttpClientHelper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.tank.common.JacksonObjectMapper;
import com.tank.domain.ImportedUnit;
import com.tank.message.schema.SchemaRes;
import com.tank.message.status.StatusRes;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
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
    val creator_id = importedUnit.getCreator_id();

    val counterSql = "select count(*) as cnt from " + table + " where recordFlag = ?";
    val parameters = new Object[]{importedUnit.getUuid()};
    Integer importedNum = this.oracleJdbcTemplate.queryForObject(counterSql, parameters, (rs, rowNum) -> rs.getInt("cnt"));
    val request = Unirest.post(endImportLogUrl)
        .field("uuid", uuid)
        .field("count", importedNum)
        .getHttpRequest();
    val sb = this.importLogMessage(creator_id);
    try {
      val statusRes = HttpClientHelper.request(request, StatusRes.class).getBody();

      if (statusRes.isSuccess()) {
        sb.append(" success imported ");
        sb.append(importedNum);
        sb.append(" records");
        logger.log(Level.INFO, sb.toString());
      }

    } catch (UnirestException e) {
      sb.append(" end imported exception:---->");
      sb.append(e.getLocalizedMessage());
      logger.log(Level.WARNING, sb.toString());
      e.printStackTrace();
    }

  }

  /**
   * 开始一个excel导入日志
   *
   * @param importedUnit
   */
  public void startImportLog(ImportedUnit importedUnit) {
    //TODO 还没有写方法体并且没有在需要的地方去调用
    Unirest.setObjectMapper(new JacksonObjectMapper());
    val table = importedUnit.getTableName();
    val uuid = importedUnit.getUuid();
    val creator_id = importedUnit.getCreator_id();
    val desc = importedUnit.getDesc();
    val request = Unirest.post(startImportLog)
        .field("table", table)
        .field("uuid", uuid)
        .field("desc", desc)
        .field("creator_id", creator_id).getHttpRequest();
    val sb = this.importLogMessage(creator_id);
    try {
      val status = HttpClientHelper.request(request, StatusRes.class).getBody();
      if (status.isSuccess()) {
        sb.append(" success start import data");
        logger.log(Level.INFO, sb.toString());
      }
    } catch (UnirestException e) {
      sb.append(" start import exception:---->");
      sb.append(e.getLocalizedMessage());
      logger.log(Level.WARNING, sb.toString());
      e.printStackTrace();
    }

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
