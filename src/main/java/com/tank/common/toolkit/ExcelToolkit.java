package com.tank.common.toolkit;

import lombok.NonNull;
import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import oracle.sql.CHAR;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author fuchun
 */
public class ExcelToolkit {

  /**
   * c1 date,
   * c2 varchar2(50),
   * c3 varchar2(50),
   * c4 varchar2(50),
   * c5 varchar2(50),
   * c6 varchar2(50),
   * c7 varchar2(50),
   * c8 NUMERIC(20,2),
   * c9 VARCHAR2(50),
   * c10 NUMERIC(20,2),
   * c11 VARCHAR2(50),
   * c12 NUMERIC(10,2),
   * c13 NUMERIC(10,2),
   * c14 NUMERIC(10,2),
   * c15 NUMERIC(10,2),
   * c16 NUMERIC(10,2),
   * c17 NUMERIC(10,2),
   * c18 NUMERIC(10,2)
   *
   * @return
   */

  public static Map<Integer, String> schema() {
    //TODO 这个是需要调用node.js的restful接口的
    Map<Integer, String> types = new HashMap<>();
    types.putIfAbsent(1, "d");
    types.putIfAbsent(2, "n");
    int start = 3;
    int end = 5;
    for (int i = start; i <= end; i++) {
      types.putIfAbsent(i, "s");
    }
    types.putIfAbsent(6, "n");
    types.putIfAbsent(7, "s");
    types.putIfAbsent(8, "n");
    types.putIfAbsent(9, "s");
    types.putIfAbsent(10, "n");
    types.putIfAbsent(11, "s");
    start = 12;
    end = 18;
    for (int i = start; i <= end; i++) {
      types.putIfAbsent(i, "n");
    }
    return types;
  }

  /**
   * 产生zip文件
   *
   * @param file
   * @return
   * @throws IOException
   */
  public static String generateZipExcel(final @NonNull File file) throws IOException {
    String path = DirectoryToolKit.downloadDir();
    val zipFile = new File(path + File.separator + file.getName().replace("xlsx", "zip"));
    Path source = Paths.get(file.toURI());
    FileOutputStream out = new FileOutputStream(zipFile);
    Files.copy(source, out);
    out.flush();
    out.close();
    return zipFile.getAbsolutePath();
  }

  /**
   * 解压excel文件
   *
   * @param filePath
   * @return
   * @throws ZipException
   */
  public static String unZipExcel(final @NonNull String filePath) throws ZipException {
    File zipFile = new File(filePath);
    String dirName = zipFile.getName().replace(".zip", "");
    val dirPath = DirectoryToolKit.downloadDir() + File.separator + dirName;
    File unZipFolder = new File(dirPath);
    if (!unZipFolder.exists()) {
      unZipFolder.mkdir();
    }
    ZipFile sourceZip = new ZipFile(zipFile);
    sourceZip.extractAll(unZipFolder.getAbsolutePath());
    return dirPath;
  }

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
  public static String converToDateStr(final @NonNull Integer dateTime) {
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
    val map = new HashMap<String, Integer>();
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


