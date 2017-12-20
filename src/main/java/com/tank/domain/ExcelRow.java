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
  private List<ExcelCell> cells = new LinkedList<>();

  public void addCell(@NonNull ExcelCell excelCell) {
    cells.add(excelCell);
  }

  public int cellsNumber() {
    return cells.size() ;
  }

  @Override
  public String toString() {

    StringBuffer sb = isHeader ? headerSql(cells) : selectSql(cells);
    return sb.toString();
  }

  private StringBuffer selectSql(@NonNull List<ExcelCell> cells) {
    StringBuffer sb = new StringBuffer("select ");
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

  private StringBuffer headerSql(@NonNull List<ExcelCell> cells) {
    StringBuffer sb = new StringBuffer("insert into tab_excel ");
    return sb;
  }
}
