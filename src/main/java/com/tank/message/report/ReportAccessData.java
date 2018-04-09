package com.tank.message.report;


import lombok.Data;

import java.util.List;

@Data

public class ReportAccessData {

    private List<String> tableNames;  //reportAccessStats  根据表名查询
    private List<String> userNames;   //reportBusinessAccessStatistics   根据用户名查询

}
