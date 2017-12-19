package com.tank.common.toolkit;

import lombok.NonNull;
import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import oracle.sql.CHAR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author fuchun
 */
public class ExcelToolkit {


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
    return result;
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


