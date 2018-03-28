package com.tank.message.schema;

import lombok.Data;

@Data
public class DropTableField {
    private String tableName;
    private String field;
}
