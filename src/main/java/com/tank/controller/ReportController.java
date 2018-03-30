package com.tank.controller;


import com.tank.common.toolkit.ReportToolkit;
import com.tank.message.report.ReportUnit;
import com.tank.message.report.ReportAccess;
import com.tank.message.report.ReportAccessData;
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

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(path = "/report", produces = APPLICATION_JSON_VALUE)
public class ReportController {

    @PostMapping(
            path = "/create_reported_access",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> reportInsertRecord(@RequestBody ReportUnit reportUnit) {

        val status = new HashMap<String, String>(16);
        try {
            this.reportToolkit.reportInsertRecord(reportUnit);
            status.putIfAbsent("success", "drop field successfully");
            return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            status.putIfAbsent("error", e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }

    @PostMapping(
            path = "/reported_access_stats",
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


    @Autowired
    private ReportToolkit reportToolkit;

}
