package com.tank.common.toolkit;

import lombok.NonNull;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author fuchun
 */
@Service
public class SchemaToolKit {


    public void createSchema(@NonNull String schemaSql) throws DataAccessException {
        this.oracleJdbcTemplate.execute(schemaSql);
    }

    public Optional<List<List<String>>> getSchemaData(String filePath) {
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
    private JdbcTemplate oracleJdbcTemplate;
}
