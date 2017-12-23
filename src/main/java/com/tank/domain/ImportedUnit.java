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
  private String creator_id = null;
  private String insertSql = null;
  private String uuid = null;

}
