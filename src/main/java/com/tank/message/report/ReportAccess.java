package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * reportAccessStats  方法
 * 本类用于接收返回的数据
 * @author xyc
 *
 */
@Data
@AllArgsConstructor   //包含所有变量
public class ReportAccess {
    private String report_name;
    private String total_report_name;  //每个表记录被查看总次数
    private String total_user;   //总用户数(有多少用户查看过)


}
