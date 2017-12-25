package com.tank.domain;

import lombok.Data;

import java.util.List;

/**
 * @author fuchun
 */
@Data
public class FieldsInfo {
  private String tableName;
  private String desc;
  private List<List<String>> fieldsInfo;

  public FieldsInfo(String tableName, String desc, List fieldsInfo) {
    this.tableName = tableName;
    this.desc = desc;
    this.fieldsInfo = fieldsInfo;
  }


}
