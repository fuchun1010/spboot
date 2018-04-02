package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ReportUser {

    //private String user_id;
    private String user_name;
    //private String count_user_name;
    private Timestamp to_char;

}
