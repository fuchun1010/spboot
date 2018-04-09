package com.tank.message.report;

import lombok.Data;

import java.util.List;

@Data
public class ReportUserAccessData {
    private List<String> userNames;   //reportUserBusinessAccess   根据用户名查询
}
