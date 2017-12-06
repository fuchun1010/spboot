package com.tank.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tank.common.DataSource.OracleJdbcTemplate;
import com.tank.domain.Header;
import com.tank.domain.Row;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

@Service
public class PriewDao {


  public void prewViewOracleTop10(@NonNull String username, @NonNull String password, @NonNull String url, @NonNull String sql) {
    JdbcTemplate jdbcTemplate = oracleJdbcTemplate.createJdbcTemple(username, password, url);
    val previewSql = oracleJdbcTemplate.wrapperPreview(sql);
    List<Row> rows = Lists.newCopyOnWriteArrayList();
    List<Header> headers = Lists.newCopyOnWriteArrayList();

    jdbcTemplate.query(previewSql, rs -> {
      if (headers.isEmpty()) {
        ResultSetMetaData metaData = rs.getMetaData();
        val columnNum = metaData.getColumnCount() - 1;
        for (int i = 1; i <= columnNum; i++) {
          val name = metaData.getColumnName(i);
          val type = metaData.getColumnTypeName(i);
          val header = new Header(type, name);
          headers.add(header);
        }
      }

      val row = new Row();
      val columnNum = headers.size();
      for (int i = 0; i < columnNum; i++) {
        val header = headers.get(i);
        val fieldName = header.getName();
        val value = rs.getObject(fieldName).toString();
        row.addValue(fieldName, value);
      }
      rows.add(row);

    });


    System.out.println("-----------------------");

  }


  @Autowired
  private OracleJdbcTemplate oracleJdbcTemplate;

}
