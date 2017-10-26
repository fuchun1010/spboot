package com.tank.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author fuchun
 */
@Configuration
@ComponentScan
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.yml"})
public class DataSourceConfig {


  @Bean(name = "jdbcTemplate")
  public JdbcTemplate initJdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean(name = "dataSource")
  public DataSource initDataSource() {
    DataSource dataSource = new DataSource();
    dataSource.setUrl(url);
    dataSource.setDriverClassName(driver);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }

  private @Value("${mysql.driver}")
  String driver;

  private @Value("${mysql.url}")
  String url;

  private @Value("${mysql.username}")
  String username;

  private @Value("${mysql.password}")
  String password;

}
