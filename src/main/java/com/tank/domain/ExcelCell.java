package com.tank.domain;


import lombok.Data;
import lombok.NonNull;
import lombok.val;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class ExcelCell {


  private String value = "";

  private String type = "";

  private String STRING_TYPE = "s";

  private String NUMBER_TYPE = "n";

  private String HEADER_TYPE = "h";


  /**
   * 是否是不带小时分秒的日期类型
   *
   * @param dateStr
   * @return
   */
  private boolean isDateWithOutHours(@NonNull String dateStr) {
    val pattern = "\\d{4}-\\d{2}-\\d{2}";
    return dateStr.matches(pattern);
  }

  private boolean isDateWithHours(@NonNull String dateStr) {
    val formatter = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(formatter);
    try {
      sdf.parse(dateStr);
      return true;
    } catch (ParseException e) {
      return false;
    }
  }

  @Override
  public String toString() {
    val rs = "'" + this.value + "'";
    val isNumberTypeOrHeader = NUMBER_TYPE.equalsIgnoreCase(type) || HEADER_TYPE.equalsIgnoreCase(type);
    if (isNumberTypeOrHeader) {
      return this.value;
    }
    boolean isDate = isDateWithOutHours(this.value) || isDateWithHours(this.value);
    if (isDate) {
      return "to_date(" + rs + ", 'yyyy-MM-dd hh24:mi:ss')";
    }
    return rs;
  }
}
