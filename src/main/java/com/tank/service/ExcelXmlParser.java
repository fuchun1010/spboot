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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import static java.io.File.separator;

/**
 * @author fuchun
 */
@Service
public class ExcelXmlParser {

  /**
   * 获取excel一行记录来初始化ExcelRow
   *
   * @param rowNode
   * @param row
   * @param fileName
   * @throws FileNotFoundException
   * @throws DocumentException
   */
  private void initExcelRow(Element rowNode, ExcelRow row, String fileName) throws FileNotFoundException, DocumentException {
    Iterator<Element> it = rowNode.elementIterator();
    while (it.hasNext()) {
      Element node = it.next();
      Iterator<Attribute> attributes = node.attributeIterator();
      ExcelCell cell = new ExcelCell();
      while (attributes.hasNext()) {
        Attribute attribute = attributes.next();
        boolean isHeader = row.isHeader;
        boolean isExistedType = "t".equalsIgnoreCase(attribute.getName());
        boolean isStrType = "s".equalsIgnoreCase(attribute.getValue());
        if (isHeader) {
          cell.setType("h");
        } else if (isExistedType && isStrType) {
          cell.setType("s");
        } else {
          cell.setType("n");
        }
      }

      Element children = (Element) node.elementIterator().next();
      Object data = children.getData();
      if (Objects.isNull(data)) {
        continue;
      }
      String value = data.toString();
      //这个地方要改
      boolean isHeaderOrStringType = "s".equalsIgnoreCase(cell.getType()) || "h".equalsIgnoreCase(cell.getType());
      if (isHeaderOrStringType) {
        val index = Integer.parseInt(value);
        String result = fetchStrContent(fileName, index);
        result = Objects.isNull(result) ? null : result;
        if (Objects.isNull(result)) {
          cell.setValue(null);
        } else {
          cell.setValue(result);
        }
      } else {
        cell.setValue(value);
      }

      row.addCell(cell);
    }
  }


  private String fetchStrContent(String fileName, int index) throws FileNotFoundException, DocumentException {
    val path = this.absoluteContentTypePath(fileName);
    if (!new File(path).exists()) {
      throw new FileNotFoundException(fileName + " not exists");
    }
    val reader = new SAXReader();
    val document = reader.read(new File(path));
    val root = document.getRootElement();
    int counter = 0;
    Iterator<Element> children = root.elementIterator();
    while (children.hasNext()) {
      Element si = children.next();
      Element t = (Element) si.elementIterator().next();
      if (counter == index) {
        val data = t.getData();
        val rs = Objects.isNull(data) ? null : data.toString();
        return rs;
      }
      counter++;
    }
    return null;
  }

  /**
   * 获取到row标记以后 对row标记下的节点进行处理
   *
   * @param element
   * @param fileName
   * @throws FileNotFoundException
   * @throws DocumentException
   */
  public void fetchRows(final Element element, String fileName) throws FileNotFoundException, DocumentException {
    Iterator<Element> it = element.elementIterator();
    List<ExcelRow> excelRows = new LinkedList<>();
    while (it.hasNext()) {
      Element item = it.next();
      boolean isRowNode = "row".equalsIgnoreCase(item.getName());
      ExcelRow row = null;
      if (isRowNode) {
        row = new ExcelRow();
        row.isHeader = excelRows.size() == 0;
        initExcelRow(item, row, fileName);
      }
      excelRows.add(row);
      //TODO 这个地方需要判断批量一次性写入的数据是否达到阀值
    }
    //给最后一行打标记,需要拼接的
    ExcelRow lastRow = excelRows.get(excelRows.size() - 1);
    lastRow.isLast = true;
    StringBuffer insertSql = new StringBuffer();
    for (ExcelRow row : excelRows) {
      insertSql.append(row.toString());
    }
    this.importSqlQueue.add(insertSql.toString());
    excelRows.clear();

  }

  /**
   * 获取到sheetData节点以后立马终止解析
   *
   * @param fileName
   * @throws DocumentException
   * @throws FileNotFoundException
   */
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
    val realPath = DirectoryToolKit.downloadDir() + separator + onlyFileName + separator + "xl" + File.separator + "sharedStrings.xml";
    val contentFile = new File(realPath);
    if (!contentFile.exists()) {
      throw new FileNotFoundException(fileName + " Content_Types.xml不正确");
    }
    return realPath;
  }

  @Autowired
  private BlockingQueue<String> importSqlQueue;

}
