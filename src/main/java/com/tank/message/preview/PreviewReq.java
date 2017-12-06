package com.tank.message.preview;

import lombok.Data;
import lombok.val;

import java.util.Optional;

/**
 * @author fuchun
 */
@Data
public class PreviewReq {

  private String sql;
  private DataSourceInfo dataSourceInfo;
}


