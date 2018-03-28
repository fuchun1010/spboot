package com.tank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PreviewTable {
    private String imported_table_name;
    private String imported_status;
    private String imported_by_email;
    private BigDecimal imported_time;
}
