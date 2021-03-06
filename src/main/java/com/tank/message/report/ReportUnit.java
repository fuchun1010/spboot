package com.tank.message.report;


import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
public class ReportUnit {

    private boolean isOver = false;
    private String user_id = null;
    private String user_name = null;
    private String report_id = null;
    private String report_name = null;
    private String user_email = null;
    private Timestamp access_time = null;

}
