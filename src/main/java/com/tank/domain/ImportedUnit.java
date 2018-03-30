package com.tank.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
public class ImportedUnit {

  private boolean isOver = false;
  private String tableName = null;
  private String desc = null;
  private String imported_desc = null;
  private String creator_email = null;
  private String insertSql = null;
  private String uuid = null;
  private String uploader_email = null;
  private int totalRows = 0;

}
