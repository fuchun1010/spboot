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

  private List<ExcelCell> cells = new LinkedList<>();

  public void addCell(@NonNull ExcelCell excelCell) {
    cells.add(excelCell);
  }

  @Override
  public String toString() {
    List<String> strs = new LinkedList<>();
    for (ExcelCell cell : cells) {
      strs.add(cell.toString());
    }
    StringBuffer sb = new StringBuffer("select ");
    sb.append(String.join(",", strs));
    sb.append(" from dual union");
    return sb.toString();
  }
}
