package com.tank.controller;

import com.tank.message.*;
import com.tank.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import static org.springframework.http.MediaType.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author fuchun
 */
@RestController
@RequestMapping(path = "/welcome")
public class WelComeController {

  @GetMapping(path = "/index",produces = APPLICATION_JSON_VALUE)
  public WelComeResponse index() {
    if(!Objects.isNull(this.orderService)) {
      this.orderService.addOrder();
    }
    WelComeResponse response = new WelComeResponse();
    response.setWords("welcome fuchun to spring boot");
    return response;
  }

  @PostMapping(path = "/users",produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<UsersResponse>> fetchUsers() {
    UsersResponse usersResponse = new UsersResponse();
    usersResponse.addUser(new User().setName("lisi").setJob("docter").setAddress(new Address().setLocation("Cq")))
        .addUser(new User().setName("wangwu").setJob("teacher").setAddress(new Address().setLocation("Bj")));
    return new ResponseEntity<>(new Response<>("", usersResponse), HttpStatus.OK);
  }

  @PostMapping(
      path = "/createUser",
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<String>> createUser(@RequestBody User user) {
    Response<String> response = new Response<>("", "");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping(
      path = "/user/{id}",
      produces = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<UserResponse>> fetchUser(@PathVariable long id) {
    System.out.println("id-------->" + id);
    UserResponse userResponse = new UserResponse();
    User user = new User();
    user.setName("lisi").setJob("driver").setAddress(new Address().setLocation("HN"));
    userResponse.setUser(user);
    return new ResponseEntity<>(
        new Response<>("", userResponse), HttpStatus.OK);
  }

  @GetMapping(
      path = "/user/id/{id}/job/{job}",
      produces = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<UserResponse>> fetchUserByJob(@PathVariable("id") String id, @PathVariable("job") String job) {
    System.out.println("id------->" + id);
    System.out.println("job------>" + job);
    UserResponse userResponse = new UserResponse();
    User user = new User();
    user.setName("lisi").setJob("driver").setAddress(new Address().setLocation("HN"));
    userResponse.setUser(user);
    return new ResponseEntity<>(
        new Response<>("", userResponse), HttpStatus.OK);
  }


  @Autowired
  private OrderService orderService;
}
