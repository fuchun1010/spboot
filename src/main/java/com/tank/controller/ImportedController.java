package com.tank.controller;

import com.tank.service.ExcelXmlParser;
import lombok.val;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.tank.common.toolkit.DirectoryToolKit.downloadDir;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * oracle 数据导入router
 *
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
    val sequenceSql = "create sequence seq_tab_users start with 1 INCREMENT BY 1 nomaxvalue cache 10000";
    val trigger = "CREATE OR REPLACE TRIGGER trigger_tab_user BEFORE INSERT ON tab_users FOR EACH ROW BEGIN SELECT seq_tab_users.nextval INTO :new.id from dual END";
    try {
      this.oracleJdbcTemplate.execute(createTableSql);
      this.oracleJdbcTemplate.execute(sequenceSql);
      this.oracleJdbcTemplate.execute(trigger);
      map.putIfAbsent("status", "created table sequence trigger success");
      return ResponseEntity.ok(map);
    } catch (DataAccessException e) {
      map.putIfAbsent("error", e.getMessage());
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(map);
    }

  }


  @PostMapping(path = "/import-data")
  public ResponseEntity<Map<String, String>> importDataFromExcel() {
    val response = new HashMap<String, String>();
    String xlsxPath = downloadDir() + "/Workbook1.xlsx";
    try {
       excelXmlParser.fetchSheetDataNode("Workbook1.xlsx");
      response.putIfAbsent("status", "ok");
    } catch (Exception e) {
      e.printStackTrace();
      response.putIfAbsent("error", e.getLocalizedMessage());
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
    return ResponseEntity.ok(response);
  }

  @Autowired
  private JdbcTemplate oracleJdbcTemplate;

  @Autowired
  private ExcelXmlParser excelXmlParser;

}
