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

import static java.io.File.*;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelXmlParser {


  private void initExcelRow(Element rowNode, ExcelRow row, String fileName) throws FileNotFoundException, DocumentException {
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
        } else {
          cell.setType("n");
        }
      }

      Element children = (Element) node.elementIterator().next();
      String value = children.getData().toString();
      if ("s".equalsIgnoreCase(cell.getType())) {
        //TODO [去content_types].xml
        val index = Integer.parseInt(value);
        String result = fetchStrContent(fileName, index);
        result = "empty".equalsIgnoreCase(result) ? null : result;
        cell.setValue(result);
      } else {

        cell.setValue(value.toString());
      }

      row.addCell(cell);
    }
  }


  private String fetchStrContent(String fileName, int index) throws FileNotFoundException, DocumentException {
    String path = this.absoluteContentTypePath(fileName);
    SAXReader reader = new SAXReader();
    Document document = reader.read(new File(path));
    Element root = document.getRootElement();
    boolean isContinue = true;
    int counter =  0;
    String rs = "";
    Iterator<Element> children = root.elementIterator();
    while(children.hasNext() && isContinue){
      Element si = children.next();
      Element t = (Element) si.elementIterator().next();
      if(counter == index) {
        val data = t.getData();
        rs = Objects.isNull(data)? "empty":data.toString();
        isContinue = false;
        continue;
      }
      counter++;
    }
    return rs;
  }

  public void fetchRows(final Element element, String fileName) throws FileNotFoundException, DocumentException {
    Iterator<Element> it = element.elementIterator();
    List<ExcelRow> excelRows = new LinkedList<>();
    while (it.hasNext()) {
      Element item = it.next();
      boolean isRowNode = "row".equalsIgnoreCase(item.getName());
      ExcelRow row = null;
      if (isRowNode) {
        row = new ExcelRow();
        initExcelRow(item, row, fileName);
      }
      excelRows.add(row);
    }
    System.out.println("-----");

  }

  public void fetchSheetDataNode(@NonNull String fileName) throws DocumentException, FileNotFoundException {
    SAXReader reader = new SAXReader();
    String sheetPath = this.absoluteSheetPath(fileName);
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

    if (!Objects.isNull(sheetData)) {
      fetchRows(sheetData, fileName);
    }

  }


  private String absoluteSheetPath(final @NonNull String fileName) throws FileNotFoundException {
    val onlyFileName = fileName.replace(".xlsx", "");
    val realPath = DirectoryToolKit.downloadDir() + separator + onlyFileName + separator + "xl" + File.separator + "worksheets";
    val sheetsDir = new File(realPath);
    if (!sheetsDir.exists()) {
      throw new FileNotFoundException(fileName + "解压处理异常");
    }
    val files = sheetsDir.listFiles();
    if (!Objects.isNull(files)) {
      return files[0].getAbsolutePath();
    }
    return "failure";
  }

  private String absoluteContentTypePath(final @NonNull String fileName) throws FileNotFoundException {
    val onlyFileName = fileName.replace(".xlsx", "");
    val realPath = DirectoryToolKit.downloadDir() + separator + onlyFileName + separator +  "xl" + File.separator + "sharedStrings.xml";
    val contentFile = new File(realPath);
    if (!contentFile.exists()) {
      throw new FileNotFoundException(fileName + " Content_Types.xml不正确");
    }
    return realPath;
  }

}
