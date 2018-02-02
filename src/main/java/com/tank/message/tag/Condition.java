package com.tank.message.tag;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Condition implements ConditionNode {


  private String compare;

  private List<String> values;

  private String field;
}
