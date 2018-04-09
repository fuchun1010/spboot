package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * reportUserLogin
 * 用来接收返回的数据
 */

@Data
@AllArgsConstructor
public class ReportUser {

    private String user_name;
    private String total_user;
    private Timestamp last_access_time;

}
