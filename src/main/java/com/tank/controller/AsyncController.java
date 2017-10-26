package com.tank.controller;

import com.tank.dao.UserDAO;
import com.tank.message.Address;
import com.tank.message.User;
import io.reactivex.Observable;
import io.reactivex.Single;

import static org.springframework.http.HttpStatus.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

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
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public DeferredResult<ResponseEntity<List<User>>> asyncUsers() {
    DeferredResult<ResponseEntity<List<User>>> deferredResult = new DeferredResult<>();
    Observable<List<User>> observable = Observable.create(emitter -> {
      List<User> users = userDAO.findAll();
      emitter.onNext(users);
    });

    observable.subscribe(
        sub -> deferredResult.setResult(new ResponseEntity<>(sub, OK)),
        deferredResult::setErrorResult
    ).dispose();

    return deferredResult;
  }

  @RequestMapping(
      path = "/sync/user",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public ResponseEntity<List<User>> syncUsers() {
    final List<User> users = userDAO.findAll();
    return new ResponseEntity<List<User>>(users, OK);
  }

  @Autowired
  private UserDAO userDAO;
}
