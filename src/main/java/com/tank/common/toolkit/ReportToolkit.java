package com.tank.common.toolkit;

import com.mashape.unirest.http.Unirest;
import com.tank.common.JacksonObjectMapper;
import com.tank.message.report.*;

import lombok.NonNull;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xyc
 */

@Service
public class ReportToolkit {

    /**
     * 报表写入数据  表名：fample_report_access_stats
     * @author xyc
     * @param reportUnit
     */
    public void reportInsertRecord(@NonNull ReportUnit reportUnit){
        Unirest.setObjectMapper(new JacksonObjectMapper());
        val tableName = reportUnit.getReport_name();
        val user_id = reportUnit.getUser_id();
        val user_name = reportUnit.getUser_name();
        val report_id = reportUnit.getReport_id();
        val user_email = reportUnit.getUser_email();

        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // access_time= dateFormat.format(now);
        val sql = "insert into fample_report_access_stats(user_id,user_name,report_id,report_name,user_email,access_time) values" +
                "( ? , ? , ? , ? , ? , ? ) ";
        try {
            Object[] params = new Object[]{
                    tableName,user_id,user_name,
                    report_id,user_email,
                    now};
            this.oracleJdbcTemplate.update(sql,params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public List<ReportAccess> reportAccessStats(@NonNull ReportAccessData reportAccessData) throws DataAccessException {

        List<String> tableNames = reportAccessData.getTableNames();
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

    /**
     * 用户报表 写入数据
     * @param reportUserUnit
     */
    public void reportInsertUser(@NonNull ReportUserUnit reportUserUnit){

        Unirest.setObjectMapper(new JacksonObjectMapper());
        val user_id = reportUserUnit.getUser_id();
        val user_name = reportUserUnit.getUser_name();

        Date now = new Date();
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
//        now = ts;
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String timestamp = dateFormat.format(ts);

        val sql = "insert into fample_user_login_statistics(user_id,user_name,access_time) values( ? , ?, ? )";
        try{
            Object[] params = new Object[]{user_id,user_name,now};
            System.out.println(now);
            this.oracleJdbcTemplate.update(sql,params);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<ReportUser> reportUserLogin(@NonNull ReportUserData reportUserData) throws DataAccessException {

        List<String> userNames = reportUserData.getUserNames();
        val sql = "select user_name,count(user_name),to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') from fample_user_login_statistics where user_name in (:usernames) " +
                "group by user_name,to_char";

        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("usernames", userNames);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());
        List<ReportUser> res  = template.query(sql,maps,rs ->{
            List<ReportUser> ReportUsers = new LinkedList<>();
            while (rs.next()) {
                ReportUser reportUser = new ReportUser(rs.getString("user_name"),rs.getTimestamp("to_char"));
                ReportUsers.add(reportUser);
            }
            return ReportUsers;
        });
        return res;
    }


    @Autowired
    private JdbcTemplate oracleJdbcTemplate;

}
