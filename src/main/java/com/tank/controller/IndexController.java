package com.tank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class IndexController {

  @RequestMapping(path = "/", method = RequestMethod.GET)
  public ResponseEntity<String> index() {
    return new ResponseEntity<String>("hello spring boot", HttpStatus.OK);
  }
}
