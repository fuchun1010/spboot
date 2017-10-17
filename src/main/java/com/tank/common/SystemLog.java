package com.tank.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SystemLog<T> {

  default Logger log(Class<T> cl) {
    return LoggerFactory.getLogger(cl);
  }
}
