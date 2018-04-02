package com.tank.controller;


import com.tank.common.toolkit.ReportToolkit;
import com.tank.message.report.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author xyc
 */

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(path = "/report", produces = APPLICATION_JSON_VALUE)
public class ReportController {

    /**
     * 报表插入数据
     * @param reportUnit
     * @author xyc
     * @return
     */
    @PostMapping(
            path = "/create-reported-access",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> reportInsertRecord(@RequestBody ReportUnit reportUnit) {

        val status = new HashMap<String, String>(16);
        try {
            this.reportToolkit.reportInsertRecord(reportUnit);
            status.putIfAbsent("success", "insert data successfully");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    /**
     * 查询报表数据
     * 报表名称、总查看次数、总用户数(有多少用户查看过)
     * @param reportAccessData
     * @return
     */
    @PostMapping(
            path = "/reported-access-stats",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ReportAccess>> reportAccessStats(@RequestBody ReportAccessData reportAccessData) {

        val status = new ArrayList<ReportAccess>();
        try{
            List<ReportAccess> list = this.reportToolkit.reportAccessStats(reportAccessData);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }


    /**
     * 用户报表插入数据
     * @param reportUserUnit
     * @return
     */
    @PostMapping(
            path = "/create-user-login",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> reportInsertUser(@RequestBody ReportUserUnit reportUserUnit) {

        val status = new HashMap<String, String>(16);
        try {
            this.reportToolkit.reportInsertUser(reportUserUnit);
            status.putIfAbsent("success", "insert data successfully");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    /**
     * 用户登录统计：日期、时间、用户、次数
     * 用户、每个用户登录的总次数，最后登录的时间
     * @param reportUserData
     * @return
     */
    @PostMapping(
            path = "/reported-user-login",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ReportUser>> reportUserLogin(@RequestBody ReportUserData reportUserData) {

        val status = new ArrayList<ReportUser>();
        try{
            List<ReportUser> list = this.reportToolkit.reportUserLogin(reportUserData);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }





    @Autowired
    private ReportToolkit reportToolkit;

}
