package com.tank.controller;

import com.tank.message.Address;
import com.tank.message.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


/**
 * @author fuchun
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/hello", produces = APPLICATION_JSON_UTF8_VALUE)
public class HelloController {

  @GetMapping(path = "/map", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Map<String, Object>> helloMap() {
    Map<String, Object> map = new ConcurrentHashMap<>();
    User userA = new User().setJob("Teacher").setName("lisi").setAddress(new Address().setLocation("BJ"));
    User userB = new User().setJob("Teacher").setName("wangwu").setAddress(new Address().setLocation("CQ"));
    List<User> users = Stream.of(userA, userB).collect(Collectors.toList());
    map.putIfAbsent("users", users);
    map.putIfAbsent("level", "senior");
    return new ResponseEntity<>(map, OK);
  }
}
