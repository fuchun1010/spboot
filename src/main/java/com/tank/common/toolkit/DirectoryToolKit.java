package com.tank.common.toolkit;

import lombok.val;

import java.io.File;

/**
 * @author fuchun
 */
public class DirectoryToolKit {

  /**
   * 获取下载目录
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

}
