package com.tank.controller;

import com.google.common.base.Strings;
import com.tank.common.toolkit.SchemaToolKit;
import com.tank.domain.FieldsInfo;
import com.tank.message.schema.DropTableField;
import com.tank.domain.PreviewTable;
import com.tank.message.schema.PreviewTableData;
import com.tank.message.schema.TableCreator;
import com.tank.message.schema.DeleteTableData;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Stream;

import static com.tank.common.toolkit.DirectoryToolKit.uploadFileAndGetPath;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
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

        String path = uploadFileAndGetPath(file, "schema");
        val requestData = new String[]{desc, tableName};
        val isValidate = Stream.of(requestData).filter(Strings::isNullOrEmpty).count() > 0;
        if (isValidate) {
            throw new IllegalArgumentException("desc is empty or tableName is empty");
        }
        Optional<List<List<String>>> opt = schemaToolKit.getSchemaData(path);
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
    public ResponseEntity<Map<String, String>> createTable(@RequestBody TableCreator tableCreator) {
        val status = new HashMap<String, String>(16);
        try {
            this.schemaToolKit.createSchema(tableCreator.getSqls());
            status.putIfAbsent("success", "create successfully");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }


    @DeleteMapping(
            path = "/drop/table-field",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> dropTableField(@RequestBody DropTableField dropTableFieldClz) {
        val tableName = dropTableFieldClz.getTableName();
        val field = dropTableFieldClz.getField();
        val status = new HashMap<String, String>(16);
        try {
            this.schemaToolKit.dropTableField(tableName, field);
            status.putIfAbsent("success", "drop field successfully");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    /**
     * 查询表前50条数据
     *
     * @param tableName
     * @param recordFlag
     * @return
     */
    @GetMapping(
            path = "/preview-data/{tableName}/{recordFlag}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<List<String>>> PreviewExcel(@PathVariable String tableName, @PathVariable String recordFlag) {
        val status = new ArrayList<List<String>>();
        try {
            List<List<String>> list = this.schemaToolKit.preViewExcel(tableName, recordFlag);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }


    /**
     * 删除表数据
     *
     * @param
     * @return
     * @author XYC
     */

    @DeleteMapping(
            path = "/drop-table-data",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> deleteTable(@RequestBody DeleteTableData tableData,
                                                           @RequestHeader(value = "email") String email) {
        val status = new HashMap<String, String>(16);
        try {
            this.schemaToolKit.deleteSchema(tableData);
            log.info("###> deleteSchema ==> by email: " + email + " , tableName: " + tableData.getTableName() + " , recordFlag: " + tableData.getRecordFlag());
            status.putIfAbsent("success", "200");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    /**
     * 获取表名上传历史信息  fsample_importing_logs
     * 上传成功数、上传总数
     *
     * @param tableName
     * @return
     * @author XYC
     */
    @GetMapping(
            path = "/preview-imported-logs/{tableName}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Map<String, String>>> importedPreviewInfo(@PathVariable String tableName) {
        val status = new ArrayList<Map<String, String>>();
        try {
            List<Map<String, String>> list = this.schemaToolKit.importedPreviewInfo(tableName);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    /**
     * 查询指定的数据记录  preview-tables-status
     * 查询表记录：添加时间为最后一次，可以查询多个表记录
     *
     * @param previewtabledata
     * @return
     * @author XYC
     */
    @PostMapping(
            path = "/preview-tables-status",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<PreviewTable>> previewTablesStatus(@RequestBody PreviewTableData previewtabledata) {

        try {
            List<PreviewTable> list = this.schemaToolKit.previewTablesStatus(previewtabledata);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<PreviewTable>());
        }
    }

    /**
     * 删除schema
     *
     * @param schema
     * @param email
     * @return
     */
    //curl -i -X POST -H "Content-Type: application/json" -H "email: seethru@fullsample.com" http://localhost:8888/excel/dropschema/test
    @PostMapping(
            path = "/dropschema/{schema}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> dropSchema(@PathVariable String schema, @RequestHeader(value = "email") String email) {

        val status = new HashMap<String, String>(16);
        try {
            this.schemaToolKit.dropSchema(schema, email);
            log.info("###> dropSchema ==> by email: " + email + " , schema: " + schema);
            status.putIfAbsent("success", "200");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    @Autowired
    private SchemaToolKit schemaToolKit;
}
