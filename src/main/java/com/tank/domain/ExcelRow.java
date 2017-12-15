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

  private List<ExcelCell> cells = new LinkedList<>();

  public void addCell(@NonNull ExcelCell excelCell) {
    cells.add(excelCell);
  }

}
