package com.tank.controller;

import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.dao.ImportLogDAO;
import com.tank.dao.SchemaDAO;
import com.tank.message.schema.SchemaRes;
import com.tank.service.ExcelXmlParser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.lingala.zip4j.core.ZipFile;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * oracle 数据导入router
 *
 * @author fuchun
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping(path = "/imported/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImportedController {

  /**
   * 将数据文件导入到对应的schema文件中去
   * curl -XPOST  -H "Content-Type: multipart/form-data" "http://localhost:8888/imported/api/import-data/1a8037ae-b9fc-4515-834e-6c11ec5eb8c3" -F "file=@/Users/fuchun/Javadone/learn/web_01/sp_bt_01/download/test.xlsx"
   *
   * @param schemaId
   * @param file
   * @return
   */
  @PostMapping(path = "/import-data/{schemaId}")
  public DeferredResult<ResponseEntity<Map<String, String>>> importDataFromExcel(
      @NonNull @PathVariable String schemaId,
      @RequestParam MultipartFile file,
      @RequestParam String desc,
      @RequestHeader(value = "email") String uploaderEmail
  ) {
    val response = new DeferredResult<ResponseEntity<Map<String, String>>>();
    val status = new HashMap<String, String>(16);


    try (ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes())) {
      Optional<SchemaRes> schemaOpt = this.schemaDAO.fetchSchemaResponse(schemaId);
      if (schemaOpt.isPresent()) {
        SchemaRes schemaRes = schemaOpt.get();
        schemaRes.setCreator_email(uploaderEmail).setImported_desc(desc);
        val fileName = file.getOriginalFilename();
        val dataDir = DirectoryToolKit.createOrGetUpLoadPath("data");
        val dataFilePath = dataDir + File.separator + fileName;

        Files.copy(in, new File(dataFilePath).toPath(), REPLACE_EXISTING);
        ZipFile zipFile = new ZipFile(dataFilePath);
        val unZipDir = DirectoryToolKit.createDataUnzipDir(dataFilePath);
        zipFile.extractAll(unZipDir);

        Executors.newCachedThreadPool().execute(() -> {
          try {
            this.excelXmlParser.importExcelToOracle(fileName, schemaRes);
          } catch (Exception e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            e.printStackTrace();
            response.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(status));
          }
        });


        if (!response.hasResult()) {
          status.putIfAbsent("status", "success");
          response.setResult(ResponseEntity.status(ACCEPTED).body(status));
        }
      }

      return response;
    } catch (Exception e) {
      status.putIfAbsent("error", e.getLocalizedMessage());
      e.printStackTrace();
      log.error(e.getMessage());
      response.setResult(ResponseEntity.status(INTERNAL_SERVER_ERROR).body(status));
      return response;
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
    val status = new HashMap<String, String>(16);
    try {
      this.importLogDAO.delImportedData(tableName, uuid);
      status.putIfAbsent("status", "success");
    } catch (Exception e) {
      log.error(e.getMessage());
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

