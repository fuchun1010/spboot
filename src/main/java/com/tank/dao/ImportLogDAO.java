package com.tank.dao;

import com.mashape.unirest.http.HttpClientHelper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.tank.common.JacksonObjectMapper;
import com.tank.domain.ImportedUnit;
import com.tank.message.status.StatusRes;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author fuchun
 */
@Service
public class ImportLogDAO {


  /**
   * 导入结束更改日志状态
   * @param importedUnit
   */
  public void endImportedLog(ImportedUnit importedUnit) {
    Unirest.setObjectMapper(new JacksonObjectMapper());
    //这个最后uuid列列名估计要写死
    val table = importedUnit.getTableName();
    val uuid = importedUnit.getUuid();
    val creator_id = importedUnit.getCreator_id();
    val counterSql = "select count(*) as cnt from " + table + " where recordFlag = ？";
    val importedNum = this.oracleJdbcTemplate.queryForObject(counterSql, new String[]{importedUnit.getUuid()}, Integer.class);
    val request = Unirest.post(startImportLogUrl)
        .field("table", table)
        .field("uuid", uuid)
        .field("creator_id", creator_id)
        .field("count", importedNum)
        .getHttpRequest();
    try {
      val statusRes = HttpClientHelper.request(request, StatusRes.class).getBody();
      if(statusRes.isSuccess()) {
        //TODO 写日志
      }
      else {
        //TODO 写日志
      }

    } catch (UnirestException  e) {
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
  }


  @Autowired
  private JdbcTemplate oracleJdbcTemplate;

  @Value("${esAgent.startImportLogUrl}")
  private String startImportLogUrl;
}
