package com.tank.controller;

import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.dao.ImportLogDAO;
import com.tank.dao.SchemaDAO;
import com.tank.message.schema.SchemaRes;
import com.tank.service.ExcelXmlParser;
import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * oracle 数据导入router
 *
 * @author fuchun
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/imported/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImportedController {

  /**
   * 将数据文件导入到对应的schema文件中去
   *
   * @param schemaId
   * @param file
   * @return
   */
  @PostMapping(path = "/import-data/{schemaId}")
  public ResponseEntity<Map<String, String>> importDataFromExcel(
      @PathVariable String schemaId,
      @RequestParam MultipartFile file
  ) {
    val response = new HashMap<String, String>();

    try (ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes())) {
      Optional<SchemaRes> schemaOpt = this.schemaDAO.fetchSchemaResponse(schemaId);
      if (schemaOpt.isPresent()) {
        SchemaRes schemaRes = schemaOpt.get();
        val fileName = file.getOriginalFilename();
        val dataDir = DirectoryToolKit.createOrGetUpLoadPath("data");
        val dataFilePath = dataDir + File.separator + fileName;

        Files.copy(in, new File(dataFilePath).toPath(), REPLACE_EXISTING);
        ZipFile zipFile = new ZipFile(dataFilePath);
        val unZipDir = DirectoryToolKit.createDataUnzipDir(dataFilePath);
        zipFile.extractAll(unZipDir);

        this.excelXmlParser.importExcelToOracle(fileName, schemaRes);
        response.putIfAbsent("status", "success");
      }
      return ResponseEntity.status(ACCEPTED).body(response);
    } catch (Exception e) {
      response.putIfAbsent("error", e.getLocalizedMessage());
      e.printStackTrace();
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
    }
  }


  /**
   * 按照uuid删除一批已经导入的数据
   *
   * @param uuid
   * @return
   */
  @DeleteMapping(path = "/delete-imported-data/{tableName}/{uuid}")
  public ResponseEntity<Map<String, String>> deleteImportedData(@PathVariable String tableName, @PathVariable String uuid) {
    val status = new HashMap<String, String>();
    try {
      this.importLogDAO.delImportedData(tableName, uuid);
      status.putIfAbsent("status", "success");
    } catch (Exception e) {
      status.putIfAbsent("status", e.getLocalizedMessage());
      return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(status);
    }
    return ResponseEntity.status(OK).body(status);
  }

  @Autowired
  private ExcelXmlParser excelXmlParser;

  @Autowired
  private ImportLogDAO importLogDAO;

  @Autowired
  private SchemaDAO schemaDAO;


}
