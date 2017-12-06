package com.tank.controller;

import static org.springframework.http.MediaType.*;

import static org.springframework.http.HttpStatus.*;

import com.tank.message.preview.PreviewReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api/elastic", produces = APPLICATION_JSON_UTF8_VALUE)
public class ElasticController {


  @PostMapping(path = "/preview", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> previewData(@RequestBody PreviewReq previewReq) {

    Map<String, String> map = new ConcurrentHashMap<>();
    map.put("properties", "elastic");
    return new ResponseEntity<Map<String, String>>(map, OK);
  }
}
