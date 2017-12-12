package com.tank.exception;

import lombok.NonNull;

/**
 * @author fuchun
 */
public class OracleSchemaException extends RuntimeException {

  public OracleSchemaException(@NonNull String message) {
    super(message);
  }
}
