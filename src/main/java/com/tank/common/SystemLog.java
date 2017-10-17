package com.tank.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fuchun
 * @param <T>
 */
public interface SystemLog<T> {

  /**
   * 获取日志
   * @param cl
   * @return
   */
  default Logger log(Class<T> cl) {
    return LoggerFactory.getLogger(cl);
  }
}
