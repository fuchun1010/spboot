package com.tank.domain;

import com.google.common.collect.Maps;

import java.util.Map;

public class Row {

  private Map<String, String> cell = Maps.newConcurrentMap();

  public void addValue(String key, String value) {
    cell.putIfAbsent(key, value);
  }

}
