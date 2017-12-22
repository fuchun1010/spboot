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
  private List<SchemaItem> types;

  public Map<Integer, String> toIndexedType() {
    Map<Integer, String> mapped = new HashMap<>();
    for (SchemaItem item : types) {
      mapped.putIfAbsent(item.getIndex(), item.getType());
    }
    return mapped;
  }
}
