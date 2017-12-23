package com.tank.common.toolkit;

import lombok.val;

import java.io.File;

import static java.io.File.separator;

/**
 * @author fuchun
 */
public class DirectoryToolKit {

  /**
   * 获取下载目录
   *
   * @return
   */
  public static String downloadDir() {
    val currentPath = new File(".");
    val absolutePath = currentPath.getAbsolutePath().replace(".", "");
    val downloadPath = absolutePath + "download/";
    val downloadDir = new File(downloadPath);
    if (!downloadDir.exists()) {
      downloadDir.mkdir();
    }
    return downloadDir.getAbsolutePath();
  }

  /**
   *
   * @param subDirName
   * @return
   */
  public static String createOrGetUpLoadPath(String subDirName) {
    val currentPath = new File(".");
    val absolutePath = currentPath.getAbsolutePath().replace(".", "");
    val uploadDirPath = absolutePath + "upload" + separator;
    val dataDirPath = uploadDirPath + subDirName + separator;
    val dataDir = new File(dataDirPath);
    if (!dataDir.exists()) {
      dataDir.mkdirs();
    }
    return dataDir.getAbsolutePath();
  }

  public static String createDataUnzipDir(String filePath) {
    val path = filePath.replace(".xlsx", "");
    val dir = new File(path);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return path;
  }


}
