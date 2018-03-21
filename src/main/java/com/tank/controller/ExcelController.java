package com.tank.controller;

import com.google.common.base.Strings;
import com.tank.common.toolkit.SchemaToolKit;
import com.tank.domain.FieldsInfo;
import com.tank.message.schema.TableCreator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Autowired
    private SchemaToolKit schemaToolKit;
}
