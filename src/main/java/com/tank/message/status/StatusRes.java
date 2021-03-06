package com.tank.message.status;

import lombok.Data;

import java.util.Objects;

/**
 * @author fuchun
 */
@Data
public class StatusRes {

  private String status;

  public boolean isSuccess() {
    return Objects.equals("success", status);
  }
}
