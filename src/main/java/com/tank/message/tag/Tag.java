package com.tank.message.tag;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fuchun
 */
@JsonDeserialize(using = TagDeserializer.class)
@Data
@Accessors(chain = true)
public class Tag implements ConditionNode {
  private String id;
  private String name;
  private String op;
  private List<ConditionNode> conditions = new LinkedList<>();

  public void add(ConditionNode node) {
    conditions.add(node);
  }
}
