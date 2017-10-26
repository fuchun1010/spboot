package com.tank.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {
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
