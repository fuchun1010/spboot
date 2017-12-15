package com.tank.controller;

import lombok.NonNull;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin
@RestController
@RequestMapping(path = "/excel", produces = APPLICATION_JSON_VALUE)
public class ExcelController {


  @PostMapping(path = "/create-download")
  public ResponseEntity<Map> createDownloadDir() {
    val downloadPath = new File(".").getAbsolutePath().replace(".", "") + "dd2/";
    val target = new File(downloadPath);
    if (!target.isDirectory()) {
      target.mkdir();
    }
    val map = new HashMap<String, String>();
    map.putIfAbsent("status", "ok");

    return new ResponseEntity<Map>(map, OK);
  }


  //curl -i -X POST -H "Content-Type: multipart/form-data" -F "file=@test.mp3" http://mysuperserver/media/1234/upload/
  @PostMapping(path = "/upload/{type}")
  public ResponseEntity<List<List<String>>> uploadFile(@RequestParam MultipartFile file
      , @PathVariable("type") String type) {
    Map<String, String> map = new HashMap<>();
    String path = uploadExcelAndGetPath(file);
    if (path == null) {
      map.putIfAbsent("errors", "upload failed.");
      return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }
    if ("schema".equalsIgnoreCase(type)) {
      List fieldsInfo = getSchemaData(path);
      if (!Objects.isNull(fieldsInfo)) {
        return new ResponseEntity<>(fieldsInfo, OK);
      }
    } else if ("data".equalsIgnoreCase(type)) {

    }
    return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
  }


  private List getSchemaData(String filePath) {
    InputStream excelFileToRead = null;
    HSSFWorkbook wb = null;
    try {
      excelFileToRead = new FileInputStream(filePath);
      wb = new HSSFWorkbook(excelFileToRead);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    List<List<String>> fieldsList = new LinkedList<>();
    HSSFSheet sheet = wb.getSheetAt(0);
    Row row;
    Cell cell;
    Iterator<Row> rows = sheet.rowIterator();
    int rowNum = 0;
    while (rows.hasNext()) {
      rowNum++;
      //只处理2-5行的
      if (rowNum > 6) {
        break;
      }
      if (1 == rowNum) {
        rows.next();
        continue;
      }
      row = rows.next();
      Iterator<Cell> cells = row.cellIterator();

      int colNum = 0;
      while (cells.hasNext()) {

        List<String> col;
        if (fieldsList.size() <= colNum || (col = fieldsList.get(colNum)) == null) {
          col = new LinkedList<>();
          fieldsList.add(colNum, col);
        }
        cell = cells.next();
        col.add(cell.toString());
        colNum++;
      }

    }
    return fieldsList.size() > 0 ? fieldsList : null;
  }

  private static String downLoadDirPath() {
    val file = new File(".");
    val absolutePath = file.getAbsolutePath().replace(".", "");
    val sb = new StringBuffer();
    sb.append(absolutePath);
    sb.append("download" + File.separator);
    return sb.toString();
  }

  static String uploadExcelAndGetPath(MultipartFile file) {
    File downloadDir = new File(downLoadDirPath());
    List<File> existedFiles = Stream.of(downloadDir.listFiles()).filter(existedFile -> existedFile.getName().equalsIgnoreCase(file.getName())).collect(Collectors.toList());
    if (existedFiles.size() == 0) {
      try {
        String fileName = downLoadDirPath() + file.getOriginalFilename();
        File tmpFile = new File(fileName);
        if (!tmpFile.exists()) {
          tmpFile.createNewFile();
        }
        byte[] data = file.getBytes();
        FileOutputStream out = new FileOutputStream(tmpFile);
        out.write(data);
        out.flush();
        out.close();
        return fileName;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private File fetchDownloadFile(@NonNull final String fileName) {
    val file = new File(".");
    val absolutePath = file.getAbsolutePath().replace(".", "");
    val sb = new StringBuffer();
    sb.append(absolutePath);
    sb.append("download/");
    sb.append(fileName);
    return new File(sb.toString());
  }

  private String checkDownloadFileExists(@NonNull final String fileName) {
    val file = fetchDownloadFile(fileName);
    return file.exists() ? file.getAbsolutePath() : "failure";
  }


  private boolean isDate(@NonNull final String value) {
    String[] patters = {"yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss"};
    boolean isOk = false;
    try {
      DateTimeFormatter.ofPattern(patters[0]).parse(value, LocalDate::from);
      isOk = true;
      if (isOk) {
        return true;
      }
      DateTimeFormatter.ofPattern(patters[1]).parse(value, LocalDate::from);
      isOk = true;
      return isOk;
    } catch (DateTimeParseException e) {
      return isOk;
    }
  }
}
