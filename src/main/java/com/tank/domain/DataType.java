package com.tank.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Optional;


/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataType {

  private String dataType;
  private boolean isRequired;
  private Optional<Integer> length;

}
