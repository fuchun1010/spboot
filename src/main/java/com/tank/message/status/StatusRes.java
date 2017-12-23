package com.tank.message.status;

import lombok.Data;

import java.util.Objects;

@Data
public class StatusRes {

  private String status;

  public boolean isSuccess() {
    return Objects.equals("success", this.status);
  }
}
