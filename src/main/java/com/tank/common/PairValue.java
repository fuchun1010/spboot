package com.tank.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PairValue<F, S> {

  public PairValue(F first, S second) {
    this.first = first;
    this.second = second;
  }

  private @NonNull F first;
  private @NonNull S second;
}
