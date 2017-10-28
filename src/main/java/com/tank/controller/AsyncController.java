package com.tank.controller;

import com.tank.dao.UserDAO;
import com.tank.message.User;
import io.reactivex.Observable;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.http.MediaType.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author fuchun
 * async router
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/v1/api")
public class AsyncController {

  @RequestMapping(
      path = "/async/user",
      method = GET,
      produces = APPLICATION_JSON_UTF8_VALUE
  )
  public DeferredResult<ResponseEntity<List<User>>> asyncUsers() {
    DeferredResult<ResponseEntity<List<User>>> deferredResult = new DeferredResult<>();
    Observable<List<User>> observable = Observable.create(emitter -> {
      val users = userDAO.findAll();
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
      method = GET,
      produces = APPLICATION_JSON_UTF8_VALUE
  )
  public ResponseEntity<List<User>> syncUsers() {
    val users = userDAO.findAll();
    return new ResponseEntity<>(users, OK);
  }

  @Autowired
  private UserDAO userDAO;
}
