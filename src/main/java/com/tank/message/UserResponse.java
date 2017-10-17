package com.tank.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
  private @NonNull User user;
}
