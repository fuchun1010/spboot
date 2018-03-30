package com.tank.common.toolkit;

import com.mashape.unirest.http.Unirest;
import com.tank.common.JacksonObjectMapper;
import com.tank.message.report.ReportUnit;
import com.tank.message.report.ReportAccess;
import com.tank.message.report.ReportAccessData;

import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class ReportToolkit {

//    public void createReport(@NonNull String reportSql) throws DataAccessException {
//        this.oracleJdbcTemplate.execute(reportSql);
//    }


    public void  reportInsertRecord(@NonNull ReportUnit reportUnit){
        Unirest.setObjectMapper(new JacksonObjectMapper());
        val tableName = reportUnit.getReport_name();
        val user_id = reportUnit.getUser_id();
        val user_name = reportUnit.getUser_name();
        val report_id = reportUnit.getReport_id();
        val user_email = reportUnit.getUser_email();
        val access_time = reportUnit.getAccess_time();
        val sql = "insert into fample_report_access_stats(user_id,user_name,report_id,report_name,user_email,access_time) values" +
                "( ? , ? , ? , ? , ? , ? ) ";
        try {
            Object[] params = new Object[]{
                    tableName,user_id,user_name,
                    report_id,user_email,
                    access_time};
            this.oracleJdbcTemplate.update(sql,params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<ReportAccess> reportAccessStats(@NonNull ReportAccessData reportAccessData) throws DataAccessException {

        List<String> tableNames = reportAccessData.getTableName();
//        val sql = "select imported_table_name,imported_status,imported_by_email,imported_time from FSAMPLE_IMPORTING_LOGS  where imported_table_name in (:tablenames) and imported_time in " +
//                "(select max(imported_time) from FSAMPLE_IMPORTING_LOGS where imported_table_name in (:tablenames) " +
//                "group by imported_table_name)";
        val sql = "select user_id,user_name,report_id,report_name from fample_report_access_stats " +
                "where report_name in (:tablenames) ";

        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("tablenames", tableNames);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());
        List<ReportAccess> res = template.query(sql, maps, rs -> {
            List<ReportAccess> ReportAccesses = new LinkedList<>();
            while (rs.next()) {
                ReportAccess reportAccess = new ReportAccess(rs.getString("user_id"), rs.getString("user_name"), rs.getString("report_id"), rs.getString("report_name"));
                ReportAccesses.add(reportAccess);
                System.out.println(ReportAccesses+"打印");
            }
            return ReportAccesses;
        });
        return res;
    }

    @Autowired
    private JdbcTemplate oracleJdbcTemplate;

}
