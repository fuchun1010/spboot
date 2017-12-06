package com.tank.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tank.common.DataSource.OracleJdbcTemplate;
import com.tank.domain.Header;
import com.tank.message.preview.PreViewRes;
import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fuchun
 */
@Service
public class PreViewDao {


  public PreViewRes preViewOracleTop10(@NonNull String username, @NonNull String password, @NonNull String url, @NonNull String sql) {
    JdbcTemplate jdbcTemplate = oracleJdbcTemplate.createJdbcTemple(username, password, url);
    val previewSql = oracleJdbcTemplate.wrapperPreview(sql);
    List<Map<String, String>> rows = Lists.newCopyOnWriteArrayList();
    List<Header> headers = Lists.newCopyOnWriteArrayList();
    val counter = new AtomicInteger();
    jdbcTemplate.query(previewSql, rs -> {
      if (headers.isEmpty()) {
        ResultSetMetaData metaData = rs.getMetaData();
        val columnNum = metaData.getColumnCount() - 1;
        for (int i = 1; i <= columnNum; i++) {
          val name = metaData.getColumnName(i).toLowerCase();
          val type = metaData.getColumnTypeName(i).toLowerCase();
          val header = new Header(type, name, name, name);
          headers.add(header);
        }
      }


      Map<String, String> row = Maps.newConcurrentMap();
      val columnNum = headers.size();
      for (int i = 0; i < columnNum; i++) {
        val header = headers.get(i);
        val fieldName = header.getName();
        val value = rs.getObject(fieldName).toString();
        row.putIfAbsent(fieldName, value);
      }
      row.putIfAbsent("key", String.valueOf(counter.incrementAndGet()));
      rows.add(row);

    });

    return new PreViewRes().setColumns(headers).setDataSource(rows);

  }


  @Autowired
  private OracleJdbcTemplate oracleJdbcTemplate;

}
