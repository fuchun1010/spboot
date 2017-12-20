package com.tank.domain;


import static com.tank.common.toolkit.DateToolkit.*;
import lombok.Data;
import lombok.val;

/**
 * @author fuchun
 */
@Data
public class ExcelCell {

  private String value = "";

  private String type = "";

  private String NUMBER_TYPE = "n";

  private String HEADER_TYPE = "h";

  private String DATE_TYPE = "d";


  @Override
  public String toString() {
    val rs = "'" + this.value + "'";
    val isNumberTypeOrHeader = NUMBER_TYPE.equalsIgnoreCase(type) || HEADER_TYPE.equalsIgnoreCase(type);
    if (isNumberTypeOrHeader) {
      return this.value;
    }
    boolean isDate = DATE_TYPE.equalsIgnoreCase(type);
    if (isDate) {
      return toDateWithTime(this.value);
    }
    return rs;
  }
}
