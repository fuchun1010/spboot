package com.tank.common.toolkit;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fuchun
 */
public class ExcelRow {
  @Override
  public String toString() {
    return String.join(",", cells);
  }

  public void addCellValue(String value) {
    this.cells.add(value);
  }

  @Getter
  private List<String> cells = new LinkedList<>();
}






