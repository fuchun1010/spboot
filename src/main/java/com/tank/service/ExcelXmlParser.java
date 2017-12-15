package com.tank.service;

import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.domain.ExcelCell;
import com.tank.domain.ExcelRow;
import lombok.NonNull;
import lombok.val;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelXmlParser {


  private void initExcelRow(Element rowNode, ExcelRow row) {
    Iterator<Element> it = rowNode.elementIterator();
    ExcelCell cell = null;
    while (it.hasNext()) {
      Element node = it.next();
      Iterator<Attribute> attributes = node.attributeIterator();
      cell = new ExcelCell();
      while (attributes.hasNext()) {
        Attribute attribute = attributes.next();
        boolean isExistedType = "t".equalsIgnoreCase(attribute.getName());
        boolean isStrType = "s".equalsIgnoreCase(attribute.getValue());
        if (isExistedType && isStrType) {
          cell.setType("s");
        }
        else {
          cell.setType("n");
        }
      }

      Element children = (Element) node.elementIterator().next();
      cell.setValue(children.getData().toString());
      System.out.println("");
      row.addCell(cell);
    }
  }

  public void fetchRows(final Element element) {
    Iterator<Element> it = element.elementIterator();
    List<ExcelRow> excelRows = new LinkedList<>();
    while (it.hasNext()) {
      Element item = it.next();
      boolean isRowNode = "row".equalsIgnoreCase(item.getName());
      ExcelRow row = null;
      if (isRowNode) {
        row = new ExcelRow();
        initExcelRow(item, row);
      }
      excelRows.add(row);
    }

    if (excelRows.size() == 2) {
      System.out.println("--------excel row------");
    }

  }

  public Element fetchSheetDataNode() throws DocumentException {
    SAXReader reader = new SAXReader();
    String sheetPath = this.absoluteSheetPath("Workbook1.xlsx");
    Document document = reader.read(new File(sheetPath));
    Element root = document.getRootElement();
    Iterator<Element> it = root.elementIterator();
    Element sheetData = null;
    boolean isContinue = true;

    while (it.hasNext() && isContinue) {
      Element item = it.next();

      boolean isRowProperty = "sheetData".equalsIgnoreCase(item.getName());
      if (!isRowProperty) {
        continue;
      }
      isContinue = false;
      sheetData = item;
    }

    return sheetData;
  }


  private String absoluteSheetPath(final @NonNull String fileName) {
    val onlyFileName = fileName.replace(".xlsx", "");
    val realPath = DirectoryToolKit.downloadDir() + File.separator + onlyFileName + File.separator + "xl" + File.separator + "worksheets";
    val sheetsDir = new File(realPath);
    val files = sheetsDir.listFiles();
    if (!Objects.isNull(files)) {
      return files[0].getAbsolutePath();
    }
    return "failure";
  }

}
