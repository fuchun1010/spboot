package com.tank.controller;

import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.dao.SchemaDAO;
import com.tank.message.schema.SchemaRes;
import com.tank.service.ExcelXmlParser;
import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * oracle 数据导入router
 *
 * @author fuchun
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/imported/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImportedController {

  @PostMapping(path = "/import-data/{schemaId}")
  public ResponseEntity<Map<String, String>> importDataFromExcel(
      @PathVariable String schemaId,
      @RequestParam MultipartFile file
  ) {
    val response = new HashMap<String, String>();
    response.putIfAbsent("status", "ok");
    try (ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes())) {
      Optional<SchemaRes> schemaOpt = this.schemaDAO.fetchSchemaResponse(schemaId);
      if (schemaOpt.isPresent()) {
        SchemaRes schemaRes = schemaOpt.get();
        Map<Integer, String> mapped = schemaRes.toIndexedType();
        val fileName = file.getOriginalFilename();
        val dataDir = DirectoryToolKit.upLoadPath("data");
        val dataFilePath = dataDir + File.separator + fileName;
        System.out.println(fileName);
        Files.copy(in, new File(dataFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
        ZipFile zipFile = new ZipFile(dataFilePath);
        val unZipDir = DirectoryToolKit.createDataUnzipDir(dataFilePath);
        zipFile.extractAll(unZipDir);
      }
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.putIfAbsent("error", e.getLocalizedMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

//    val name = parameters.get("name");
//    val map = ExcelToolkit.schema();
//    String fileName = Objects.isNull(name) ? "Workbook2.xlsx" : name;
//    try {
//      excelXmlParser.importExcelToOracle(fileName,map);
//
//    } catch (Exception e) {
//      e.printStackTrace();
//      response.putIfAbsent("error", e.getLocalizedMessage());
//      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
//    }

  }


  @Autowired
  private ExcelXmlParser excelXmlParser;

  @Autowired
  private SchemaDAO schemaDAO;

}
