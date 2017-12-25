package com.tank.message.status;

import lombok.Data;

import java.util.Objects;

@Data
public class StatusRes {

  private int status;

  public boolean isSuccess() {
    return status == 200 || status == 201;
  }
}
