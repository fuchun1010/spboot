package com.tank.message.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 接收返回的数据
 * @author xyc
 *
 */
@Data
@AllArgsConstructor
public class ReportAccess {

    private String user_id;
    private String user_name;
    private String report_id;
    private String report_name;


}
