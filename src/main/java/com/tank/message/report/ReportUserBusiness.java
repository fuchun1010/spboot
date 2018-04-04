package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ReportUserBusiness {

    private String user_name;
    private String report_name;
    private Timestamp last_access_time;

}
