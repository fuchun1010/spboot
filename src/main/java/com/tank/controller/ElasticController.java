package com.tank.controller;

import com.tank.dao.PreViewDao;
import com.tank.message.preview.PreViewRes;
import com.tank.message.preview.PreviewReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author fuchun
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/elastic", produces = APPLICATION_JSON_UTF8_VALUE)
public class ElasticController {


  @PostMapping(path = "/preview", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<PreViewRes> previewData(@RequestBody PreviewReq previewReq) {

    Map<String, String> map = new ConcurrentHashMap<>();
    map.put("properties", "elastic");
    String sql = previewReq.getSql();
    PreViewRes response = preViewDao.preViewOracleTop10(sql);
    return new ResponseEntity<>(response, OK);

  }

  @Autowired
  private PreViewDao preViewDao;
}
