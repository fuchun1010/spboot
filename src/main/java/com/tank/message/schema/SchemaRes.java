package com.tank.message.schema;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
public class SchemaRes {
  private String table;
  private String creator_email;
  private String uploader_email;
  private String imported_desc;
  private String desc;
  private List<SchemaItem> types;

  public Map<Integer, String> toIndexedType() {
    Map<Integer, String> mapped = new HashMap<>();
    for (SchemaItem item : types) {
      String type = item.getType();
      if (type.indexOf("date") != -1) {
        type = "d";
      } else if (type.indexOf("varchar2") != -1) {
        type = "s";
      } else {
        type = "n";
      }

      mapped.putIfAbsent(item.getIndex(), type);
    }
    return mapped;
  }
}
