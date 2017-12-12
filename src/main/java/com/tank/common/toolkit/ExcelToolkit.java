package com.tank.common.toolkit;

import com.tank.exception.ExcelNotFoundException;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static com.tank.common.toolkit.DateToolkit.*;

/**
 * @author fuchun
 */
public interface ExcelToolkit {

  default List<ExcelRow> readExcel(@NonNull String excelFilePath) throws IOException, InvalidFormatException {
    File file = new File(excelFilePath);
    if (!file.exists()) {
      throw new ExcelNotFoundException(excelFilePath + " not exists");
    }
    val rows = new LinkedList<ExcelRow>();
    XSSFWorkbook wb = new XSSFWorkbook(file);
    //Workbook wb = new SXSSFWorkbook(xlsxFile,-1);

    Iterator<Row> sheetRows = wb.getSheetAt(0).rowIterator();
    while (sheetRows.hasNext()) {
      Row row = sheetRows.next();
      if(row.getRowNum() == 0) {
        continue;
      }
      Iterator<Cell> rowCells = row.cellIterator();
      ExcelRow excelRow = new ExcelRow();
      while (rowCells.hasNext()) {
        Cell cell = rowCells.next();
        boolean isNumeric = cell.getCellTypeEnum() == CellType.NUMERIC;
        if (isNumeric) {
          excelRow.addCellValue(String.valueOf(cell.getNumericCellValue()));
        } else {
          String cellValue = cell.getStringCellValue();
          if (Objects.isNull(cellValue) || cellValue.isEmpty()) {
            excelRow.addCellValue(null);
            continue;
          }
          if (isDate(cellValue)) {
            if(isBasicDate(cellValue)) {
              excelRow.addCellValue(toBasicDate(cellValue));
            }
            else {
              excelRow.addCellValue(toDateWithTime(cellValue));
            }
            continue;
          }
          excelRow.addCellValue('"'+cellValue+'"');
        }
      }
      rows.add(excelRow);
    }
    return rows;
  }

}


