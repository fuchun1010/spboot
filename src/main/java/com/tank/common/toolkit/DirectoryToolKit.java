package com.tank.common.toolkit;

import lombok.val;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
  public static String createLogDir(final String dirName) {
    val currentPath = new File(".");
    val absolutePath = currentPath.getAbsolutePath().replace(".", "");
    val downloadPath = absolutePath + dirName + File.separator;
    val logDir = new File(downloadPath);
    if (!logDir.exists()) {
      logDir.mkdirs();
    }
    return logDir.getAbsolutePath();
  }

  /**
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

  /**
   * 上传文件并获取路径
   *
   * @param file
   * @return
   */
  public static String uploadFileAndGetPath(MultipartFile file, String subDirName) {
    val schemaDirPath = DirectoryToolKit.createOrGetUpLoadPath(subDirName);
    String uploadFilePath = schemaDirPath + File.separator + file.getOriginalFilename();
    File uploadedFile = new File(uploadFilePath);
    try (ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes())) {
      Files.copy(in, uploadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return uploadFilePath;
  }


}
