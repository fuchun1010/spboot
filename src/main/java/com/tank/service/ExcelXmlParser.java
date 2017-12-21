package com.tank.service;

import com.tank.common.toolkit.DirectoryToolKit;
import com.tank.common.toolkit.ExcelToolkit;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

import static java.io.File.separator;

/**
 * @author fuchun
 */
@Service
public class ExcelXmlParser {

  /**
   * 将excel的数据导入oracle数据库
   *
   * @param fileName
   */
  public void importExcelToOracle(@NonNull final String fileName, Map<Integer, String> schema) throws FileNotFoundException, DocumentException {
    Element sheetDataNode = fetchSheetDataNode(fileName);
    if (Objects.isNull(sheetDataNode)) {
      throw new DocumentException("sheetData node没有找到");
    }
    composeSqlStatement(sheetDataNode, fileName, schema);
  }

  /**
   * 产生空的单元格
   *
   * @param differ
   * @return
   */
  private List<ExcelCell> generateNullCells(final int differ) {
    List<ExcelCell> cells = new LinkedList<>();
    for (int i = 0; i < differ; i++) {
      ExcelCell cell = new ExcelCell();
      cell.setType("n");
      cell.setValue(null);
      cells.add(cell);
    }
    return cells;
  }

  /**
   * 获取excel一行记录来初始化ExcelRow
   *
   * @param rowNode
   * @param fileName
   * @return
   * @throws FileNotFoundException
   * @throws DocumentException
   */
  private ExcelRow initExcelRow(Element rowNode, String fileName, Map<Integer, String> schema, Map<Integer,String> sharedStrMap) throws FileNotFoundException, DocumentException {
    Iterator<Element> it = rowNode.elementIterator();
    ExcelRow row = new ExcelRow();
    while (it.hasNext()) {
      Element node = it.next();
      Attribute attribute = node.attribute("r");
      ExcelCell cell = new ExcelCell();
      row.isHeader = false;

      String cellColumn = attribute.getValue();
      val cellPosition = ExcelToolkit.excelCellPosition(cellColumn);
      val cellType = schema.get(cellPosition);
      cell.setType(cellType);

      Iterator<Element> c = node.elementIterator();
      if (!c.hasNext()) {
        val excelCells = generateNullCells(1);
        for (ExcelCell tmpCell : excelCells) {
          row.addCell(tmpCell);
        }
        continue;
      }


      Element children = (Element) node.elementIterator().next();
      Object data = children.getData();
      if (Objects.isNull(data)) {
        continue;
      }
      String value = data.toString();

      boolean isString = "s".equalsIgnoreCase(cell.getType());
      boolean isDateType = "d".equalsIgnoreCase(cell.getType());

      if (isDateType) {
        val result = Objects.isNull(value) ? null : ExcelToolkit.converToDateStr(Integer.parseInt(value));
        cell.setValue(result);
      } else if (isString) {
        val index = Integer.parseInt(value);
        String result =  sharedStrMap.get(index);
        result = Objects.isNull(result) ? null : result;
        cell.setValue(result);
      } else {
        cell.setValue(value);
      }

      row.addCell(cell);
    }
    return row;
  }


  /**
   * 获取excel的列数
   *
   * @param sheetData
   * @param fileName
   * @return
   */
  private int getColumns(final Element sheetData, final String fileName) throws FileNotFoundException, DocumentException {
    Iterator<Element> it = sheetData.elementIterator();
    Element item = it.next();
    Iterator<Element> cells = item.elementIterator();
    int counter = 0;
    while (cells.hasNext()) {
      cells.next();
      ++counter;
    }
    return counter;
  }

  /**
   * 将sql发送到队列接收写入操作
   *
   * @param excelRows
   */
  private void sendExcelRowsToQueue(List<ExcelRow> excelRows) {
    val isNotEmpty = !Objects.isNull(excelRows) && excelRows.size() > 1;
    if (isNotEmpty) {
      //给最后一行打标记,最后一行和其他行需要拼接的内容是不一致的
      ExcelRow lastRow = excelRows.get(excelRows.size() - 1);
      lastRow.isLast = true;
      StringBuffer insertSql = new StringBuffer();
      for (ExcelRow tmpRow : excelRows) {
        insertSql.append(tmpRow.toString());
      }
      this.importSqlQueue.add(insertSql.toString());
      ExcelRow header = excelRows.get(0);
      excelRows.clear();
      excelRows.add(header);
    }
  }

  /**
   * 获取到row标记以后 对row标记下的节点进行处理
   *
   * @param sheetData
   * @param fileName
   * @throws FileNotFoundException
   * @throws DocumentException
   */
  private void composeSqlStatement(final Element sheetData, final String fileName, final Map<Integer, String> schema) throws FileNotFoundException, DocumentException {
    val start= System.currentTimeMillis();
    Iterator<Element> it = sheetData.elementIterator();
    List<ExcelRow> excelRows = new LinkedList<>();

    int totalColumn = getColumns(sheetData, fileName);
    val shareStrMap = this.sharedStrMapped(fileName);
    while (it.hasNext()) {
      Element item = it.next();
      val isHeaderRow = excelRows.size() == 0;
      ExcelRow row = isHeaderRow ? headerRow(item) : initExcelRow(item, fileName, schema, shareStrMap);
      int cellsDiffer = totalColumn - row.cellsNumber();
      //补缺乏的单元格(后面缺乏)
      if (cellsDiffer > 0) {
        List<ExcelCell> tmpCells = generateNullCells(cellsDiffer);
        for (ExcelCell tmp : tmpCells) {
          row.addCell(tmp);
        }
      }

      excelRows.add(row);
      val isFull = excelRows.size() == this.threshold + 1;
      if (isFull) {
        sendExcelRowsToQueue(excelRows);
      }
    }
    //可能还有剩余的数据没有处理
    sendExcelRowsToQueue(excelRows);
    //清空缓存
    shareStrMap.clear();
    val end = System.currentTimeMillis();
    System.out.println("total cost--->" + (end - start));
  }

  private ExcelRow headerRow(Element item) {
    ExcelRow row = new ExcelRow();
    row.isHeader = true;
    Iterator<Element> cells = item.elementIterator();
    while (cells.hasNext()) {
      ExcelCell cell = new ExcelCell();
      row.addCell(cell);
      cells.next();
    }
    return row;
  }

  /**
   * 获取到sheetData节点以后立马终止解析
   *
   * @param fileName
   * @throws DocumentException
   * @throws FileNotFoundException
   */
  private Element fetchSheetDataNode(@NonNull String fileName) throws DocumentException, FileNotFoundException {
    SAXReader reader = new SAXReader();

    String sheetPath = this.absoluteSheetPath(fileName);
    Document document = reader.read(new FileInputStream(new File(sheetPath)));
    Element root = document.getRootElement();
    Iterator<Element> it = root.elementIterator();

    while (it.hasNext()) {
      Element item = it.next();
      boolean isSheetDataNode = "sheetData".equalsIgnoreCase(item.getName());
      if (isSheetDataNode) {
        return item;
      }
    }

    return null;
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

  /**
   * 将sharedStrings.xml的内容缓存起来
   * @param fileName
   * @return
   * @throws FileNotFoundException
   * @throws DocumentException
   */
  private Map<Integer, String> sharedStrMapped(final @NonNull String fileName) throws FileNotFoundException, DocumentException {
    Map<Integer, String> mapped = new HashMap<>();
    val path = this.absoluteContentTypePath(fileName);
    if (!new File(path).exists()) {
      throw new FileNotFoundException(fileName + " not exists");
    }
    val reader = new SAXReader();
    val document = reader.read(new File(path));
    val root = document.getRootElement();
    int index = 0;
    Iterator<Element> children = root.elementIterator();
    while (children.hasNext()) {
      Element si = children.next();
      Element t = (Element) si.elementIterator().next();
      val data = t.getData();
      val rs = Objects.isNull(data) ? null : data.toString();
      mapped.putIfAbsent(index, rs);
      index++;
    }
    return mapped;
  }

  @Autowired
  private BlockingQueue<String> importSqlQueue;

  @Value("${oracle.threshold}")
  private int threshold;

}
