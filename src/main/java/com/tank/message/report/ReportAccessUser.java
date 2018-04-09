package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * reportBusinessAccessStatistics  方法
 * 用于接收返回来的数据
 */
@Data
@AllArgsConstructor
public class ReportAccessUser {
    private String report_name;
    private String total_report_name;
    private String total_user;

}
