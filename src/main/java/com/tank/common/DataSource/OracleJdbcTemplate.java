package com.tank.common.DataSource;

import org.springframework.stereotype.Service;

/**
 * @author fuchun
 */
@Service
public class OracleJdbcTemplate extends CustomerDataSource {

  public String wrapperPreview(String sql) {
    StringBuffer sb = new StringBuffer();
    sb.append("select fullSample.*, rownum ");
    sb.append("from ( ");
    sb.append(sql);
    sb.append(" ) fullSample ");
    sb.append("where rownum <= 10 ");
    return sb.toString();
  }

  public String wrapperPreview(String sql, int size) {
    StringBuffer sb = new StringBuffer();
    sb.append("select fullSample.*, rownum ");
    sb.append("from ( ");
    sb.append(sql);
    sb.append(" ) fullSample ");
    sb.append("where rownum <= " + size);
    return sb.toString();
  }


  @Override
  protected String getDriver() {
    return "oracle.jdbc.driver.OracleDriver";
  }
}
