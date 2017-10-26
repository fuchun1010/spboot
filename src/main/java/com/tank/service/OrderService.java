package com.tank.service;

import com.tank.common.SystemLog;
import org.springframework.stereotype.Service;

/**
 * @author fuchun
 */
@Service
public class OrderService implements SystemLog<OrderService> {


  public void addOrder() {
    this.log(OrderService.class).info("order service method: addOrder");
  }


}
