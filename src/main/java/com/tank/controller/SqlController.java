package com.tank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/sql/queue")
public class SqlController {

  @GetMapping(path = "/triggerQueue", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> triggerQueue() {
    Map<String, String> map = new HashMap<>();
    map.putIfAbsent("status", "ok");
    importSqlQueue.add("insert into table xxx");
    return ResponseEntity.status(ACCEPTED).body(map);
  }


  @GetMapping(path = "/parse")
  public ResponseEntity<Map<String, String[]>> parseRow() {
    Map<String, String[]> rows = new HashMap<>();
    rows.putIfAbsent("rows", new String[]{});

    return ResponseEntity.status(ACCEPTED).body(rows);
  }

  @Autowired
  private BlockingQueue<String> importSqlQueue;


}
