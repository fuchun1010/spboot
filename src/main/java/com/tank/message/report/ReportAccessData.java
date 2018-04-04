package com.tank.message.report;


import lombok.Data;

import java.util.List;

@Data
public class ReportAccessData {

    private List<String> tableNames;
    private List<String> userNames;

}
