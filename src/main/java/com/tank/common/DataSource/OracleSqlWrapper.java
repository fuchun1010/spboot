package com.tank.common.DataSource;

import org.springframework.stereotype.Service;

/**
 * @author fuchun
 */
@Service
public class OracleSqlWrapper {

  public String wrapperPreview(String sql) {
    StringBuffer sb = this.composeSqlBody(sql);
    sb.append("where rownum <= 10 ");
    return sb.toString();
  }

  private StringBuffer composeSqlBody(String sql) {
    StringBuffer sb = new StringBuffer();
    sb.append("select fullSample.*, rownum ");
    sb.append("from ( ");
    sb.append(sql);
    sb.append(" ) fullSample ");
    return sb;
  }
}
