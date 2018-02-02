package com.tank.controller;


import com.tank.message.tag.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author fuchun
 */
@Slf4j
@Controller
@CrossOrigin
@RequestMapping(path = "/tag", produces = APPLICATION_JSON_VALUE)
public class SpikeController {

  @PostMapping(path = "/")
  public ResponseEntity<String> index(@RequestBody Tag tag) {
    return ResponseEntity.status(HttpStatus.OK).body("spike router");
  }
}
