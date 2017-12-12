package com.tank.config;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan
@EnableTransactionManagement
@EnableAutoConfiguration
public class OracleDataSourceConfig {


  @Bean(name = "oracleJdbcTemplate")
  public JdbcTemplate createOracleDataSourceTemplate(DataSource oracleDataSource) {
    return new JdbcTemplate(oracleDataSource);
  }

  @Bean(name = "oracleDataSource")
  @Primary
  public DataSource initOracleDataSource() {
    DataSource dataSource = new DataSource();
    dataSource.setUrl(url);
    dataSource.setDriverClassName(driver);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }


  @Value("${oracle.driver}")
  private String driver;
  @Value("${oracle.url}")
  private String url;
  @Value("${oracle.username}")
  private String username;
  @Value("${oracle.password}")
  private String password;
}
