package com.tank.message.preview;

import com.tank.domain.Header;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author fuchun
 */
@Data
@Accessors(chain = true)
public class PreViewRes {

  private List<Map<String, String>> dataSource;
  private List<Header> columns;
}
