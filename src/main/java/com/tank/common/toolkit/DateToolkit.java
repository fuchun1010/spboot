package com.tank.common.toolkit;

import lombok.NonNull;
import lombok.val;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author fuchun
 */
public class DateToolkit {

  public static String toDateWithTime(@NonNull String dateStr) {
    return "TO_DATE('" + dateStr + "', 'YYYY-MM-DD HH24:mi:ss')";
  }

}
