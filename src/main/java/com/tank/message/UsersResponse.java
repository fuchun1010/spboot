package com.tank.message;

import com.tank.common.ThreadSafe;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fuchun
 */
@ThreadSafe
public class UsersResponse {

  @Getter
  private List<User> users = new CopyOnWriteArrayList<>();

  public UsersResponse addUser(User user) {
    this.users.add(user);
    return this;
  }
}






