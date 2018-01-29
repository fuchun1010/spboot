package com.tank.common.toolkit;

import lombok.NonNull;
import lombok.val;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author fuchun
 */
public class ExcelToolkit {

  /**
   * 根据xml中c标记的字符串获取位置
   *
   * @param cellNo
   * @return
   */
  public static Integer excelCellPosition(final @NonNull String cellNo) {
    val mapped = createMapped();
    val tmpCellNo = extractCharacter(cellNo.toUpperCase());
    val queue = toQueue(tmpCellNo);
    val result = calculatePosition(queue, mapped);
    mapped.clear();
    return result;
  }

  /**
   * 将excel中日期字段的数据转成日期字符串(yyyy-MM-dd HH:mm:ss)
   *
   * @param dateTime
   * @return
   */
  public static String convert2DateStr(final @NonNull Integer dateTime) {
    Calendar calendar = DateUtil.getJavaCalendar(dateTime);
    Date date = calendar.getTime();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = df.format(date);
    return dateStr;
  }

  /**
   * 创建映射关系，A-Z,A从1开始
   *
   * @return
   */
  private static Map<String, Integer> createMapped() {
    val map = new HashMap<String, Integer>(1024);
    val start = 'A';
    val end = 'Z';
    int counter = 1;
    for (char ch = start; ch <= end; ch++) {
      map.putIfAbsent(String.valueOf(ch), counter);
      counter++;
    }
    return map;
  }

  /**
   * 过滤字符串中的数字类型
   *
   * @param cellNo
   * @return
   */
  private static String extractCharacter(final @NonNull String cellNo) {
    int len = cellNo.length();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < len; i++) {
      char ch = cellNo.charAt(i);
      val isNumber = Character.isDigit(ch);
      if (!isNumber) {
        sb.append(ch);
      }
    }
    return sb.toString();
  }


  /**
   * 将位置字符串转换为队列AB=>A, B
   *
   * @param cellNo
   * @return
   */
  private static Queue<String> toQueue(String cellNo) {
    Queue<String> queue = new ConcurrentLinkedQueue<>();
    int len = cellNo.length();
    for (int i = 0; i < len; i++) {
      char ch = cellNo.charAt(i);
      queue.add(String.valueOf(ch));
    }
    return queue;
  }

  /**
   * 计算单元格位置
   * 输入AA 返回27
   * 输入AAW 返回725
   *
   * @param queue
   * @param map
   * @return
   */
  private static Integer calculatePosition(Queue<String> queue, Map<String, Integer> map) {
    int sum = 0;
    int len = queue.size();
    String data = queue.poll();
    if (len > 1) {
      sum += map.get(data) * Math.pow(26, len - 1);
      return sum + calculatePosition(queue, map);
    } else {
      sum += map.get(data);
      return sum;
    }
  }
}


