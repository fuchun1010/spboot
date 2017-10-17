package com.tank.message;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class UsersResponse {


  @Getter
  private List<User> users = new CopyOnWriteArrayList<>();

  public UsersResponse addUser(User user) {
    this.users.add(user);
    return this;
  }
}






