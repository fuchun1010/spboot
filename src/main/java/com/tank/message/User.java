package com.tank.message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author fuchun
 */
@Getter
@Setter
@Accessors(chain = true)
public class User {
  private String job;
  private String name;
  private Address address;

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(" job:");
    sb.append(this.job);
    sb.append(" name:");
    sb.append(this.name);
    sb.append(" " + this.address.toString());
    return sb.toString();
  }
}
