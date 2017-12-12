package com.tank.exception;

import lombok.NonNull;

public class ExcelNotFoundException extends RuntimeException {

  public ExcelNotFoundException(@NonNull String message) {
    super(message);
  }
}
