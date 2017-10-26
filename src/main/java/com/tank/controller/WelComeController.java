package com.tank.controller;

import com.tank.message.*;
import com.tank.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author fuchun
 */
@CrossOrigin
@RestController
public class WelComeController {

  @RequestMapping(path = "/index", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<WelComeResponse> index() {
    WelComeResponse response = new WelComeResponse("welcome fuchun to spring boot");
    return new ResponseEntity<WelComeResponse>(response, HttpStatus.OK);
  }

  @RequestMapping(path = "/users", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<UsersResponse>> fetchUsers() {
    UsersResponse usersResponse = new UsersResponse();
    usersResponse.addUser(new User().setName("lisi").setJob("docter").setAddress(new Address().setLocation("Cq")))
        .addUser(new User().setName("wangwu").setJob("teacher").setAddress(new Address().setLocation("Bj")));
    return new ResponseEntity<>(new Response<>("", usersResponse), HttpStatus.OK);
  }

  @RequestMapping(
      path = "/createUser",
      method = RequestMethod.POST,
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<String>> createUser(@RequestBody User user) {
    Response<String> response = new Response<>("", "");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(
      path = "/user/{id}",
      method = RequestMethod.GET,
      produces = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<User>> fetchUser(@PathVariable long id) {
    System.out.println("id-------->" + id);
    User user = new User();
    user.setName("lisi").setJob("driver").setAddress(new Address().setLocation("HN"));

    return new ResponseEntity<>(
        new Response<>("", user), HttpStatus.OK);
  }

  @RequestMapping(
      method = RequestMethod.GET,
      path = "/user/id/{id}/job/{job}",
      produces = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Response<User>> fetchUserByJob(@PathVariable("id") String id, @PathVariable("job") String job) {
    System.out.println("id------->" + id);
    System.out.println("job------>" + job);
    User user = new User();
    user.setName("lisi").setJob("driver").setAddress(new Address().setLocation("HN"));
    return new ResponseEntity<>(
        new Response<>("", user), HttpStatus.OK);
  }


  @Autowired
  private OrderService orderService;
}
