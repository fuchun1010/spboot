package com.tank.domain;


import lombok.Data;

@Data
public class ExcelCell {


  private String value = "";

  private String type = "";

  @Override
  public String toString() {
    return this.value;
  }
}
