package com.tank.controller;

import com.tank.dao.PriewDao;
import com.tank.message.preview.DataSourceInfo;
import com.tank.message.preview.PreviewReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/elastic", produces = APPLICATION_JSON_UTF8_VALUE)
public class ElasticController {


  @PostMapping(path = "/preview", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> previewData(@RequestBody PreviewReq previewReq) {

    Map<String, String> map = new ConcurrentHashMap<>();
    map.put("properties", "elastic");
    String sql = previewReq.getSql();
    DataSourceInfo dataSourceInfo = previewReq.getDataSourceInfo();
    priewDao.prewViewOracleTop10(dataSourceInfo.getUsername(), dataSourceInfo.getPassword(), dataSourceInfo.toUrl().orElse(""), sql);
    return new ResponseEntity<Map<String, String>>(map, OK);
  }

  @Autowired
  private PriewDao priewDao;
}
