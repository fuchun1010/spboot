package com.tank.common.DataSource;

import lombok.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;

import org.apache.tomcat.jdbc.pool.DataSource;

public abstract class CustomerDataSource {

  public JdbcTemplate createJdbcTemple(@NonNull String username, @NonNull String password, @NonNull String url){
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.createDataSource(username, password, url));
    return jdbcTemplate;
  }


  protected abstract String getDriver();

  private  DataSource createDataSource(String username, String password, String url){
    DataSource dataSource = new DataSource();
    dataSource.setUrl(url);
    dataSource.setDriverClassName(this.getDriver());
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }
}
