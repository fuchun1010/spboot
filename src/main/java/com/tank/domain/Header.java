package com.tank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author fuchun
 */
@Data
@AllArgsConstructor
public class Header {

  private String type;
  private String name;
  private String title;
  private String key;
}
