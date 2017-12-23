package com.tank.controller;

import com.tank.message.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
//    Observable<List<User>> observable = Observable.create(emitter -> {
//      val users = userDAO.findAll();
//      emitter.onNext(users);
//    });
//
//    observable.subscribe(
//        sub -> deferredResult.setResult(new ResponseEntity<>(sub, OK)),
//        deferredResult::setErrorResult
//    ).dispose();

    return deferredResult;
  }

}
