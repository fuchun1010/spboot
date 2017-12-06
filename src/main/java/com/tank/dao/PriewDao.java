package com.tank.dao;

import com.google.common.collect.Lists;
import com.tank.common.DataSource.OracleJdbcTemplate;
import com.tank.domain.Header;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.List;

@Service
public class PriewDao {


  public void prewViewOracleTop10(@NonNull String username, @NonNull String password, @NonNull String url, @NonNull String sql) {
    JdbcTemplate jdbcTemplate = oracleJdbcTemplate.createJdbcTemple(username, password, url);
    val sqlStatement = oracleJdbcTemplate.wrapperPreview(sql);
    val metaDataSql = oracleJdbcTemplate.wrapperPreview(sql, 1);
    List<Header> headers = Lists.newCopyOnWriteArrayList();
    jdbcTemplate.query(metaDataSql, rs -> {
      ResultSetMetaData metaData = rs.getMetaData();
      val columnNum = metaData.getColumnCount() - 1;
      for (int i = 1; i <= columnNum; i++) {
        val name = metaData.getColumnName(i);

        val type = metaData.getColumnTypeName(i);
        val header = new Header(type, name);
        headers.add(header);
      }
    });

    
  }


  @Autowired
  private OracleJdbcTemplate oracleJdbcTemplate;

}
