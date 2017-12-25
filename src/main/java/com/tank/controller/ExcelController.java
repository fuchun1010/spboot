package com.tank.controller;

import com.google.common.base.Strings;
import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.common.toolkit.SchemaToolKit;
import com.tank.domain.FieldsInfo;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin
@RestController
@RequestMapping(path = "/excel", produces = APPLICATION_JSON_VALUE)
public class ExcelController {


  //curl -i -X POST -H "Content-Type: multipart/form-data" -F "file=@test.mp3" http://mysuperserver/media/1234/upload/
  @PostMapping(path = "/upload/{type}")
  public @ResponseBody
  FieldsInfo uploadFile(@RequestParam MultipartFile file,
                        @PathVariable("type") String type,
                        @RequestParam String tableName,
                        @RequestParam String desc) {

    String path = DirectoryToolKit.uploadFileAndGetPath(file, "schema");
    val requestData = new String[]{desc, tableName};
    val isValidate = Stream.of(requestData).filter(Strings::isNullOrEmpty).count() == 0;
    if (isValidate) {
      throw new IllegalArgumentException("desc is empty or tableName is empty");
    }
    Optional<List<List<String>>> opt = getSchemaData(path);
    if (opt.isPresent()) {
      return new FieldsInfo(tableName, type, opt.get());
    } else {
      throw new IllegalArgumentException("url type is not 'schema' or 'data' ");
    }

  }

  @PostMapping(
      path = "/createSchema",
      produces = APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Map<String, String>> createTable(@RequestBody List<String> sqls) {
    val status = new HashMap<String, String>(16);
    try {
      for (String sql : sqls) {
        schemaToolKit.createSchema(sql);
      }
      status.putIfAbsent("success", "create successfully");
      return ResponseEntity.status(HttpStatus.OK).body(status);
    } catch (DataAccessException e) {
      status.putIfAbsent("error", e.getLocalizedMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
    }
  }

  private Optional<List<List<String>>> getSchemaData(String filePath) {
    Workbook wb;
    Sheet sheet;
    Iterator<Row> rows;
    List<List<String>> fieldsList = new LinkedList<>();
    boolean isXlsxFile = filePath.endsWith("xlsx");
    try (InputStream excelFileToRead = new FileInputStream(filePath)) {
      wb = isXlsxFile ? new XSSFWorkbook(excelFileToRead) : new HSSFWorkbook(excelFileToRead);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    sheet = wb.getSheetAt(0);
    rows = sheet.rowIterator();
    Row row;
    Cell cell;
    int rowNum = 0;
    while (rows.hasNext()) {
      row = rows.next();
      if (rowNum == 0) {
        ++rowNum;
        continue;
      }
      Iterator<Cell> cells = row.cellIterator();
      List<String> col = new ArrayList<>();
      while (cells.hasNext()) {
        cell = cells.next();
        col.add(cell.toString());
      }
      fieldsList.add(col);

    }
    return Optional.ofNullable(fieldsList);
  }


  @Autowired
  private SchemaToolKit schemaToolKit;
}
