package com.tank.common.toolkit;

import lombok.val;

import java.io.File;

/**
 * @author fuchun
 */
public interface DirectoryToolKit {

  /**
   * 获取下载目录
   * @return
   */
  default String downloadDir() {
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
