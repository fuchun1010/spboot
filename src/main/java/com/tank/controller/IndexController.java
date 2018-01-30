package com.tank.controller;

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

/**
 * @author fuchun
 */
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
      return new ResponseEntity<String>("oracle conn exception", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @Autowired
  private JdbcTemplate oracleJdbcTemplate;
}
