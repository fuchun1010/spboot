package com.tank.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
public class DataType {

  private String dataType;
  private boolean isRequired;
  private Optional<Integer> length;
}
