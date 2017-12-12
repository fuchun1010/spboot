package com.tank.common.toolkit;

import lombok.NonNull;
import lombok.val;

/**
 * @author fuchun
 */
public class DateToolkit {

  public static boolean isDate(@NonNull String dateStr) {
    return isBasicDate(dateStr) || isDateWithTime(dateStr) ;
  }

  public static boolean isBasicDate(@NonNull String dateStr) {
    val basicDatePattern = "\\d{4}-\\d{2}-\\d{2}";
    if (dateStr.matches(basicDatePattern)) {
      return true;
    }
    return false;
  }

  public static boolean isDateWithTime(@NonNull String dateStr) {
    val datePattenWithTime = "\\d{4}-\\d{2}-\\d{2} ([0-1]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)";
    if (dateStr.matches(datePattenWithTime)) {
      return true;
    }
    return false;
  }

  public static String toBasicDate(@NonNull String dateStr) {
    return "TO_DATE('"+dateStr+"', 'YYYY-MM-DD')";
  }

  public static String toDateWithTime(@NonNull String dateStr) {
    return "TO_DATE('"+dateStr+"', 'YYYY-MM-DD HH24:mi:ss')";
  }

}
