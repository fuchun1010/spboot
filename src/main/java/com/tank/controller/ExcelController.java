package com.tank.controller;

import com.tank.common.toolkit.SchemaToolKit;
import com.tank.domain.FieldsInfo;
import lombok.NonNull;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public @ResponseBody
    FieldsInfo uploadFile(@RequestParam MultipartFile file,
                          @PathVariable("type") String type, @RequestParam String tableName, @RequestParam String desc,
                          HttpServletResponse response) {
        String path = uploadExcelAndGetPath(file);
        if (path == null) {
            throw new IllegalArgumentException("uploadExcelAndGetPath did not get filePath");
        }
        if ("".equalsIgnoreCase(desc)) {
            throw new IllegalArgumentException("desc is empty");
        }
        if ("".equalsIgnoreCase(tableName)) {
            throw new IllegalArgumentException("tableName is empty");
        }
        if ("schema".equalsIgnoreCase(type)) {
            List fieldsInfo = getSchemaData(path);
            if (!Objects.isNull(fieldsInfo)) {
                return new FieldsInfo(tableName, type, fieldsInfo);
            }
        } else if ("data".equalsIgnoreCase(type)) {
            response.setStatus(200);
            return null;
        }
        throw new IllegalArgumentException("url type is not 'schema' or 'data' ");
    }

    @PostMapping(
            path = "/createSchema",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> createTable(@RequestBody List<String> sqls) {
        Map<String, String> status = new HashMap<>();

//        String sql = cSql.getSql();
//        List<String> sqls = cSql.getSqls();
        try {
            for(String sql: sqls) {
                schemaToolKit.createSchema(sql);
            }
            status.putIfAbsent("success", "create successfully");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    private List getSchemaData(String filePath) {
        InputStream excelFileToRead = null;
        HSSFWorkbook wb = null;
        XSSFWorkbook xssfWorkbook = null;
        boolean isXlsx = false;
        try {
            excelFileToRead = new FileInputStream(filePath);
            if (filePath.endsWith("xlsx")) {
                isXlsx = true;
                xssfWorkbook = new XSSFWorkbook(excelFileToRead);

            } else if (filePath.endsWith("xls")){
                wb = new HSSFWorkbook(excelFileToRead);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        List<List<String>> fieldsList = new LinkedList<>();
        Iterator<Row> rows = null;
        if (isXlsx) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            rows = xssfSheet.rowIterator();
        } else {
            HSSFSheet sheet = wb.getSheetAt(0);
            rows = sheet.rowIterator();
        }
        Row row;
        Cell cell;
        int rowNum = 0;
        while (rows.hasNext()) {
            rowNum++;
            row = rows.next();
            if (rowNum > 1) {
                Iterator<Cell> cells = row.cellIterator();
                List<String> col = new ArrayList<>();
                while(cells.hasNext()) {
                    cell = cells.next();
                    col.add(cell.toString());
                }
                fieldsList.add(col);

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

    @Autowired
    private SchemaToolKit schemaToolKit;
}
