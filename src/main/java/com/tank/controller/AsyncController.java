package com.tank.controller;

import com.tank.message.Address;
import com.tank.message.User;
import io.reactivex.Single;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author fuchun
 * async router
 */
@CrossOrigin
@RestController
public class AsyncController {

  @RequestMapping(
      path = "/async/user",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public DeferredResult<ResponseEntity<User>> asyncUser() {
    DeferredResult<ResponseEntity<User>> deferredResult = new DeferredResult<>();
    Single<User> single = Single.create(emitter -> {
      User user = initSingleUser();
      emitter.onSuccess(user);
    });
    single.subscribe(
        sub -> deferredResult.setResult(new ResponseEntity<>(sub, HttpStatus.OK)),
        e -> deferredResult.setErrorResult(e)
    );
    return deferredResult;
  }

  private User initSingleUser() {
    User user = new User();
    user.setName("lisi").setJob("driver").setAddress(new Address().setLocation("Beijing"));
    return user;
  }
}
