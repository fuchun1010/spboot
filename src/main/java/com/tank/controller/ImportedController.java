package com.tank.controller;

import com.tank.common.toolkit.ExcelToolkit;
import com.tank.service.ExcelXmlParser;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * oracle 数据导入router
 *
 * @author fuchun
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/imported/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImportedController {

  @PostMapping(path = "/import-data")
  public ResponseEntity<Map<String, String>> importDataFromExcel(@RequestBody Map<String, String> parameters) {
    val response = new HashMap<String, String>();
    val name = parameters.get("name");
    val map = ExcelToolkit.schema();
    String fileName = Objects.isNull(name) ? "Workbook2.xlsx" : name;
    try {
      excelXmlParser.importExcelToOracle(fileName,map);
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
