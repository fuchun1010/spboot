package com.tank.message;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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

  private @NotNull T data;
  private String error;
}
