package com.tank.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * oracle 数据导入router
 * @author fuchun 
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/imported/api")
public class ImportedController {

  @PostMapping(path = "/create-schema")
  public ResponseEntity<Map<String, String>> createTableWithSchema() {
    val map = new HashMap<String, String>();
    val createTableSql = "create table tab_user(id int primary key,name varchar2(50) not null)";
    try {
      this.oracleJdbcTemplate.execute(createTableSql);
      map.putIfAbsent("status", "created");
      return ResponseEntity.ok(map);
    } catch (DataAccessException e) {
      map.putIfAbsent("error", e.getMessage());
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(map);
    }

  }

  @Autowired
  private JdbcTemplate oracleJdbcTemplate;

}
