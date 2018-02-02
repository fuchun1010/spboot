package com.tank.message.tag;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author fuchun 
 */
@Data
public class Group implements ConditionNode {

  private String id;

  private String op;

  private List<Condition> conditions = new LinkedList<>();
}
