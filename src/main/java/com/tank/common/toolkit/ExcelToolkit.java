package com.tank.common.toolkit;

import com.tank.exception.ExcelNotFoundException;
import lombok.NonNull;
import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fuchun
 */
public class ExcelToolkit {

  public static List<ExcelRow> readExcel(@NonNull String excelFilePath) throws IOException, InvalidFormatException,ZipException {
    File file = new File(excelFilePath);
    if (!file.exists()) {
      throw new ExcelNotFoundException(excelFilePath + " not exists");
    }
    val rows = new LinkedList<ExcelRow>();
    //OPCPackage opc = OPCPackage.open(excelFilePath, PackageAccess.READ)

    val zipFilePath = generateZipExcel(file);
    unZipExcel(zipFilePath);
    return rows;
  }


  private static String generateZipExcel(File file) throws IOException {
    String path = DirectoryToolKit.downloadDir();
    val zipFile = new File(path + File.separator + file.getName().replace("xlsx", "zip"));
    Path source = Paths.get(file.toURI());
    FileOutputStream out = new FileOutputStream(zipFile);
    Files.copy(source, out);
    out.flush();
    out.close();
    return zipFile.getAbsolutePath();
  }

  private static String unZipExcel(String filePath) throws ZipException {
    File zipFile = new File(filePath);
    String dirName = zipFile.getName().replace(".zip", "");
    val dirPath = DirectoryToolKit.downloadDir() + File.separator + dirName;
    File unZipFolder = new File(dirPath);
    if (!unZipFolder.exists()) {
      unZipFolder.mkdir();
    }
    ZipFile sourceZip = new ZipFile(zipFile);
    sourceZip.extractAll(unZipFolder.getAbsolutePath());
    return "";
  }

}


