package com.tank.controller;

import com.tank.domain.ImportedUnit;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

/**
 * @author fuchun
 */
@Slf4j
@CrossOrigin
@RestController
public class IndexController {

  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ResponseEntity<String> index() {
    return new ResponseEntity<String>("hello spring boot", HttpStatus.OK);
  }

  @RequestMapping(path = "/health", method = RequestMethod.GET)
  public ResponseEntity<String> checkOracleConn() {

    try (Connection conn = this.oracleJdbcTemplate.getDataSource().getConnection();) {
      return new ResponseEntity<String>("oracle connect ok", HttpStatus.OK);
    } catch (SQLException e) {
      e.printStackTrace();
      log.error(e.getMessage());
      return new ResponseEntity<String>("oracle conn exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(path = "/queue", method = RequestMethod.GET)
  public ResponseEntity<String> checkImportingQueue() {
    val size = importSqlQueue.size();
    return new ResponseEntity<String>("importing queue size:" + size, HttpStatus.OK);
  }

  @Autowired
  private JdbcTemplate oracleJdbcTemplate;

  @Autowired
  private BlockingQueue<ImportedUnit> importSqlQueue;
}
