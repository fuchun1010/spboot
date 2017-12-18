package com.tank.domain;

import java.util.List;

public class FieldsInfo {
    private String tableName;
    private String desc;
    private List<List<String>> fieldsInfo;
    public FieldsInfo(String tableName, String desc, List fieldsInfo){
        this.tableName = tableName;
        this.desc = desc;
        this.fieldsInfo = fieldsInfo;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<List<String>> getFieldsInfo() {
        return fieldsInfo;
    }

    public void setFieldsInfo(List<List<String>> fieldsInfo) {
        this.fieldsInfo = fieldsInfo;
    }
}
