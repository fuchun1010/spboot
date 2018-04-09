package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * reportUserBusinessAccess  方法
 * 用于接收返回来的数据
 */

@Data
@AllArgsConstructor
public class ReportUserBusiness {

    private String user_name;
    private String report_name;
    private Timestamp last_access_time;

}
