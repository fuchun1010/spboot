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

  public boolean isHeader = false;
  public boolean isLast = false;

  private List<ExcelCell> cells = new LinkedList<>();

  public void addCell(@NonNull ExcelCell excelCell) {
    cells.add(excelCell);
  }

  @Override
  public String toString() {

    StringBuffer sb = isHeader ? headerSql(cells) : selectSql(cells);
    return sb.toString();
  }

  private StringBuffer selectSql(@NonNull List<ExcelCell> cells) {
    StringBuffer sb = new StringBuffer("select ");
    List<String> strs = new LinkedList<>();
    for (ExcelCell cell : cells) {
      strs.add(cell.toString());
    }
    sb.append(String.join(",", strs));
    if (isLast) {
      sb.append(" from dual ");
    } else {
      sb.append(" from dual union ");
    }
    return sb;
  }

  private StringBuffer headerSql(@NonNull List<ExcelCell> cells) {
    StringBuffer sb = new StringBuffer("insert into tab_test( ");
    List<String> strs = new LinkedList<>();
    for (ExcelCell cell : cells) {
      strs.add(cell.toString());
    }
    sb.append(String.join(",", strs));
    sb.append(")");
    return sb;
  }
}
