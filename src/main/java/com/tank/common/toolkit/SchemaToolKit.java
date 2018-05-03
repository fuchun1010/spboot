package com.tank.common.toolkit;


import com.tank.domain.PreviewTable;
import com.tank.message.schema.DeleteTableData;
import com.tank.message.schema.PreviewTableData;
import lombok.NonNull;
import lombok.val;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


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
     *
     * @param tableData
     * @throws DataAccessException
     * @author XYC
     */
    public void deleteSchema(@NonNull DeleteTableData tableData) throws DataAccessException {

        String tableName = tableData.getTableName();
        String recordFlag = tableData.getRecordFlag();
        try {
            String sql = "delete from " + tableName + " where recordFlag = ? ";
            Object[] params = {recordFlag};
            this.oracleJdbcTemplate.update(sql,params);
            this.oracleJdbcTemplate.update("update FSAMPLE_IMPORTING_LOGS set visible=0 where record_flag = ?", params);
        }catch(DataAccessException e){
            e.printStackTrace();
        }

    }

    public void dropTableField(@NonNull String tableName, @NonNull String field) throws DataAccessException{
        this.oracleJdbcTemplate.execute("alter table " + tableName +  " drop column " + field);
    }

    /**
     * 查询表前50条数据，在预览表的时候不展示recordFlag 这个字段
     *
     * @throws DataAccessException
     */
    public List<List<String>> preViewExcel(@NonNull String tableName, @NonNull String recordFlag) throws DataAccessException {

        try {
            Object[] params = new Object[]{recordFlag};
            Object list = this.oracleJdbcTemplate.query("select * from " + tableName + " where recordFlag = ? and rownum <51 ", params, new ResultSetExtractor<Object>() {
                public List extractData(ResultSet rs) throws SQLException, DataAccessException {
                    ResultSetMetaData data = rs.getMetaData();
                    List<List<String>> list = new ArrayList<>();
                    List<String> list1 = new ArrayList<>();
                    int recordFlagIndex = -1;
                    for (int i = 1; i <= data.getColumnCount(); i++) {
                        String columnName = data.getColumnName(i);
                        if ("RECORDFLAG".equalsIgnoreCase(columnName)) {
                            recordFlagIndex = i;
                        } else {
                            list1.add(columnName);
                        }

                    }
                    list.add(list1);
                    while (rs.next()) {
                        List<String> list2 = new ArrayList<>();
                        for (int i = 1; i <= data.getColumnCount(); i++) {
                            if (recordFlagIndex > 0 && i == recordFlagIndex) {
                                continue;
                            }
                            val columnVal = rs.getObject(i);
                            if (null == columnVal) {
                                list2.add("");
                            } else {
                                list2.add(columnVal.toString());
                            }

                        }
                        list.add(list2);
                    }
                    return list;
                }
            });
            List<List<String>> rows = (List<List<String>>) list;
            return rows;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取表名上传历史信息  fsample_importing_logs
     * 上传成功数、上传总数
     * @param tableName
     * @return
     * @author XYC
     */
    public List<Map<String, String>> importedPreviewInfo(@NonNull String tableName) throws DataAccessException {
        Object[] params = new Object[]{tableName};
        Object list = this.oracleJdbcTemplate.query(" select RECORD_FLAG,IMPORTED_TABLE_NAME,imported_desc,imported_status,imported_by_email," +
                        "TOTAL_RECORDS,SUCCESS_RECORDS,IMPORTED_TIME from FSAMPLE_IMPORTING_LOGS where imported_table_name = ? and visible = 1 ",
                params, (rs) -> {
                    ResultSetMetaData data = rs.getMetaData();
                    List<Map<String, String>> list1 = new ArrayList<>();
                    while (rs.next()) {
                        Map<String, String> map = new HashMap<>();
                        for (int i = 1; i <= data.getColumnCount(); i++) {
                            String keyName = data.getColumnName(i);
                            String valueName = rs.getString(keyName);
                            map.put(keyName.toLowerCase(), valueName);
                        }
                        list1.add(map);
                    }
                    return list1;
                });
        List<Map<String, String>> rows = (List<Map<String, String>>) list;
        return rows;
    }

    /**
     * 查询指定的数据记录  preview-tables-status
     * 查询表记录：添加时间为最后一次，可以查询多个表记录
     * @param  previewtabledata
     * @return
     * @author XYC
     */
    public List<PreviewTable> previewTablesStatus(@NonNull PreviewTableData previewtabledata) throws DataAccessException {

        List<String> tableNames = previewtabledata.getTableNames();
        val sql = "select imported_table_name,imported_status,imported_by_email,imported_time from FSAMPLE_IMPORTING_LOGS  where imported_table_name in (:tablenames) and imported_time in " +
                "(select max(imported_time) from FSAMPLE_IMPORTING_LOGS where visible = 1 and imported_table_name in (:tablenames) " +
                "group by imported_table_name)";

        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("tablenames", tableNames);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());

        List<PreviewTable> res = template.query(sql, maps, rs -> {
            List<PreviewTable> PreviewTables = new LinkedList<>();
            while (rs.next()) {
                PreviewTable previewTable = new PreviewTable(rs.getString("imported_table_name"), rs.getString("imported_status"), rs.getString("imported_by_email"), rs.getBigDecimal("imported_time"));
                PreviewTables.add(previewTable);
            }
            return PreviewTables;
        });
        return res;
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

    /**
     * 删除schema
     *
     * @param schema
     * @param email
     * @throws DataAccessException
     */
    public void dropSchema(@NonNull String schema, String email) throws DataAccessException {
            val sql = "drop table " + schema;

            this.oracleJdbcTemplate.execute(sql);
            this.oracleJdbcTemplate.update("insert into FSAMPLE_DROP_LOGS(drop_schema_name,drop_by_email,droped_time) values('" + schema + "','" + email + "',sysdate)");

    }

    @Autowired
    private JdbcTemplate oracleJdbcTemplate;
}
