package com.tank.common.toolkit;


import com.tank.message.schema.DeleteTableData;

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
import org.springframework.jdbc.core.ResultSetExtractor;
//import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.lang.String;


/**
 * @author fuchun
 */
@Service
public class SchemaToolKit {


    public void createSchema(@NonNull String schemaSql) throws DataAccessException {
        this.oracleJdbcTemplate.execute(schemaSql);
    }

    /**
     * 删除表里面的数据
     * @author XYC
     * @param  tableData
     * @throws DataAccessException
     */
    public void deleteSchema(@NonNull DeleteTableData tableData) throws DataAccessException{

        String tableName = tableData.getTableName();
        String recordFlag = tableData.getRecordFlag();
        try {
            String sql = "delete from " + tableName + " where recordFlag = ? ";
            Object[] params = {recordFlag};
            this.oracleJdbcTemplate.update(sql,params);
        }catch(DataAccessException e){
            e.printStackTrace();
        }

    }

    /**
     * 表格  查询数据
     * @throws DataAccessException
     */
    public List<List<String>> preViewExcel(@NonNull String tableName, @NonNull String recordFlag) throws DataAccessException{

        try {
            Object[] params = new Object[]{recordFlag};
            Object list = this.oracleJdbcTemplate.query("select * from " + tableName + " where recordFlag = ? ", params, new ResultSetExtractor<Object>() {
                  public List extractData(ResultSet rs) throws SQLException, DataAccessException {
                      ResultSetMetaData data = rs.getMetaData();
                      List<List<String>> list = new ArrayList<>();
                      List<String> list1 = new ArrayList<>();
                      for (int i=1; i<=data.getColumnCount(); i++){
                          String columnName = data.getColumnName(i);
                          list1.add(columnName);
                      }
                      list.add(list1);
                      while(rs.next()) {
                          List<String> list2 = new ArrayList<>();
                          for (int i = 1; i<=data.getColumnCount(); i++){
                              String columnValue = rs.getObject(i).toString();
                              list2.add(columnValue);
                          }
                          list.add(list2);
                      }
                      return list;
                  }
            });
            List<List<String>> rows = (List<List<String>>)list;
            return rows;
        }catch(DataAccessException e){
            e.printStackTrace();
        }
        return null;
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
