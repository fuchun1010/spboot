package com.tank.domain;

import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Excel的行
 *
 * @author fuchun
 */
public class ExcelRow {

  public boolean isHeader = true;
  public boolean isLast = false;

  private String delimiter = ",";
  public String tableName = null;
  private List<ExcelCell> cells = new LinkedList<>();

  public void addCell(@NonNull ExcelCell excelCell) {
    cells.add(excelCell);
  }

  public int cellsNumber() {
    return cells.size();
  }

  @Override
  public String toString() {

    StringBuilder sb = isHeader ? headerSql() : selectSql(cells);
    return sb.toString();
  }

  private StringBuilder selectSql(@NonNull List<ExcelCell> cells) {
    StringBuilder sb = new StringBuilder();
    sb.append("select ");
    List<String> cellValues = new LinkedList<>();
    for (ExcelCell cell : cells) {
      cellValues.add(cell.toString());
    }
    sb.append(String.join(delimiter, cellValues));
    if (isLast) {
      sb.append(" from dual ");
    } else {
      sb.append(" from dual union ");
    }
    return sb;
  }

  private StringBuilder headerSql() {
    StringBuilder sb = new StringBuilder();
    sb.append("insert into ");
    sb.append(tableName);
    sb.append(" ");
    return sb;
  }
}
