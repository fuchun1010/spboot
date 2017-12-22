package com.tank.controller;

import com.tank.message.Address;
import com.tank.message.User;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.OK;
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

  @GetMapping(path = "/taskQueue", produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Map<String, String>> queued() {
    val result = new ConcurrentHashMap<String, String>();
    taskProcessor.submit(() -> {
      while (true) {
        System.out.println(System.currentTimeMillis());
      }
    });
    result.putIfAbsent("status", "success");
    return new ResponseEntity<>(result, OK);
  }

  @Autowired
  private ExecutorService taskProcessor;


}
