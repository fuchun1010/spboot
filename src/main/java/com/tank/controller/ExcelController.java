package com.tank.controller;

import com.tank.domain.DataType;
import lombok.NonNull;
import lombok.val;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin
@RestController
@RequestMapping(path = "/excel", produces = APPLICATION_JSON_VALUE)
public class ExcelController {

  @GetMapping(path = "/schema", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map> getSchema() {
    val fileName = "Workbook.xlsx";
    Map tips = null;
    val fileStatus = checkDownloadFileExists(fileName);
    val failure = "failure";
    if (failure.equalsIgnoreCase(fileStatus)) {
      tips = new HashMap<String, String>();
      tips.putIfAbsent("status", "failure");
      return new ResponseEntity(tips, INTERNAL_SERVER_ERROR);
    }
    tips = fetchSchema(fileStatus);
    return new ResponseEntity(tips, OK);
  }

  private Map<String, DataType> fetchSchema(@NonNull final String filePath) {
    val firstLineNo = 0;
    boolean isStar = true;
    val map = new HashMap<String, DataType>();
    val field = "c";
    int counter = 1;

    try (FileInputStream in = new FileInputStream(filePath)) {

      XSSFWorkbook book = new XSSFWorkbook(in);
      Iterator<Sheet> sheets = book.sheetIterator();
      while (sheets.hasNext() && isStar) {
        Sheet sheet = sheets.next();
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext() && isStar) {
          Row row = rows.next();
          if (row.getRowNum() == firstLineNo) {
            continue;
          }
          Iterator<Cell> cells = row.cellIterator();
          while (cells.hasNext()) {
            Cell cell = cells.next();
            DataType dataType = parseType(cell);
            map.putIfAbsent(field + counter, dataType);
            counter++;
          }
          isStar = false;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return map;
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

  private DataType parseType(@NonNull final Cell cell) {
    val dataType = new DataType();
    if ("NUMERIC".equalsIgnoreCase(cell.getCellTypeEnum().name())) {
      return dataType.setDataType("decimal").setLength(Optional.of(10));
    }
    if (isDate(cell.getStringCellValue())) {
      return dataType.setDataType("date");
    }
    return dataType.setDataType("varchar2").setLength(Optional.of(10));
  }

  private boolean isNumeric(@NonNull final String value) {
    return value.matches("^[-+]?\\d+(\\.\\d+)?$");
  }

  private boolean isDate(@NonNull final String value) {
    String[] patters = {"yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss"};
    try {
      Stream.of(patters).forEach(pattern -> DateTimeFormatter.ofPattern(pattern).parse(value, LocalDate::from));
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
