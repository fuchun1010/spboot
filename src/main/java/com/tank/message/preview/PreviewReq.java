package com.tank.message.preview;

import lombok.Data;
import lombok.val;

import java.util.Optional;

/**
 * @author fuchun
 */
@Data
public class PreviewReq {

  private String sql;
  private DataSourceInfo dataSourceInfo;
}

@Data
class DataSourceInfo {

  private String username;
  private String password;
  private String host;
  private String port;
  private String database;
  private String databaseType;

  public Optional<String> toUrl() {

    val oracleDb = "oracle";
    if (oracleDb.equalsIgnoreCase(databaseType)) {
      String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
      return Optional.of(url);
    }

    return Optional.empty();
  }
}
