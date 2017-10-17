package com.tank.message;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author fuchun
 */
@Setter
@Getter
@Accessors(chain = true)
public class Address {

  private String location;

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(" location is:");
    sb.append(this.location);
    return sb.toString();
  }
}
