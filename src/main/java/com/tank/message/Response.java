package com.tank.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Optional;

/**
 * @author fuchun
 * @param <T>
 */
@Getter
@Setter
public class Response<T> {

  public Response(String error, T data) {
    this.error = Optional.of(error).isPresent() ? error:"";
    this.data = data;
  }

  private @NonNull T data;
  private String error;
}
