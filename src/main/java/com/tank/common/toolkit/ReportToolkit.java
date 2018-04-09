package com.tank.common.toolkit;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xyc
 */

@Service
public class ReportToolkit {

    /**
     * 报表插入数据  表名：fample_report_access_stats
     * @author xyc
     * @param reportUnit
     */
    public void reportInsertRecord(@NonNull ReportUnit reportUnit){
        //获取值
        val tableName = reportUnit.getReport_name();
        val user_id = reportUnit.getUser_id();
        val user_name = reportUnit.getUser_name();
        val report_id = reportUnit.getReport_id();
        val user_email = reportUnit.getUser_email();

        // 后台自动获取当前时间，添加到params里面   前端不需要传入
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date now = ts;
        val sql = "insert into fample_report_access_stats(user_id,user_name,report_id,report_name,user_email,access_time) values" +
                "( ? , ? , ? , ? , ? , ? ) ";
        try {
            Object[] params = new Object[]{
                    tableName,user_id,user_name,
                    report_id,user_email,
                    now};
            this.oracleJdbcTemplate.update(sql,params);  //params里面的now替换sql里access_time 的占位符 ？传入当前时间
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询报表数据
     * 报表名记录、每个表记录被查看总次数、总用户数(有多少用户查看过)、最后查看的时间
     * @param reportAccessData
     * @return
     * @throws DataAccessException
     */
    public List<ReportAccess> reportAccessStats(@NonNull ReportAccessData reportAccessData) throws DataAccessException {

        List<String> tableNames = reportAccessData.getTableNames();
        // as 后面的取名跟 ReportAccess 里面 变量名一致
        val sql = "select report_name,count(report_name) as total_report_name,count(user_name) as total_user from " +
                "fample_report_access_stats group by report_name ";

        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("tablenames", tableNames);
        // 数据库获得数据  NamedParameterJdbcTemplate可以使用全部jdbcTemplate方法
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());
        List<ReportAccess> res = template.query(sql, maps, rs -> {
            List<ReportAccess> ReportAccesses = new LinkedList<>();
            while (rs.next()) {
                // 返回来的 数据在 ReportAccess 里     rs.getString  得到变量的值
                ReportAccess reportAccess = new ReportAccess(rs.getString("report_name"), rs.getString("total_report_name"), rs.getString("total_user"));
                ReportAccesses.add(reportAccess);
            }
            return ReportAccesses;
        });
        return res;
    }

    /**
     * 业务访问统计：用户名、次数、报表数
     * @param reportAccessData
     * @return
     * @throws DataAccessException
     */
    public List<ReportAccessUser> reportBusinessAccessStatistics(@NonNull ReportAccessData reportAccessData) throws DataAccessException {

        List<String> userNames = reportAccessData.getUserNames();
        // as 后面的取名跟 ReportAccess 里面 变量名一致
        val sql = "select user_name,count(user_name) as total_user,count(report_name) as total_report_name " +
                "from fample_report_access_stats group by user_name  ";

        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("usernames", userNames);
        // 数据库获得数据  NamedParameterJdbcTemplate可以使用全部jdbcTemplate方法
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());
        List<ReportAccessUser> res = template.query(sql, maps, rs -> {
            List<ReportAccessUser> ReportAccessUsers = new LinkedList<>();
            while (rs.next()) {
                // 返回来的 数据在 ReportAccessUser 里     rs.getString  得到变量的值
                ReportAccessUser reportAccessuser = new ReportAccessUser(rs.getString("user_name"),rs.getString("total_user"),rs.getString("total_report_name"));
                ReportAccessUsers.add(reportAccessuser);
            }
            return ReportAccessUsers;
        });
        return res;
    }

    /**
     * 报表访问统计：日期、时间、用户、访问报表名称；
     * @param reportUserAccessData
     * @return
     * @throws DataAccessException
     */
    public List<ReportUserBusiness> reportUserBusinessAccess(@NonNull ReportUserAccessData reportUserAccessData) throws DataAccessException {
        List<String> userNames = reportUserAccessData.getUserNames();
        val sql = "select user_name,report_name,max(to_char(access_time,'yyyy-mm-dd hh24:mi:ss')) as last_access_time from fample_report_access_stats " +
                "group by user_name,report_name ";

        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("usernames", userNames);
        // 数据库获得数据  NamedParameterJdbcTemplate可以使用全部jdbcTemplate方法
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());
        List<ReportUserBusiness> res = template.query(sql, maps, rs -> {
            List<ReportUserBusiness> report = new LinkedList<>();
            while (rs.next()) {
                ReportUserBusiness rub = new ReportUserBusiness(rs.getString("user_name"),rs.getString("report_name"),rs.getTimestamp("last_access_time"));
                report.add(rub);
            }
            return report;
        });
        return res;
    }

    /**
     * 用户报表插入数据
     * @param reportUserUnit
     */
    public void reportInsertUser(@NonNull ReportUserUnit reportUserUnit){

        val user_id = reportUserUnit.getUser_id();
        val user_name = reportUserUnit.getUser_name();

        // 后台自动获取当前时间，添加到params里面   前端不需要传入
        Timestamp ts = new Timestamp(System.currentTimeMillis()); //毫秒
        Date now = ts;
        val sql = "insert into fample_user_login_statistics(user_id,user_name,access_time) values( ? , ?, ? )";
        try{
            Object[] params = new Object[]{user_id,user_name,now};
            this.oracleJdbcTemplate.update(sql,params);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 用户登录统计：日期、时间、用户、次数
     * 用户、每个用户登录的总次数，最后登录的时间
     * @param reportUserData
     * @return
     * @throws DataAccessException
     */
    public List<ReportUser> reportUserLogin(@NonNull ReportUserData reportUserData) throws DataAccessException {

        List<String> userNames = reportUserData.getUserNames();
        val sql = "select user_name,count(user_name) as total_user,max(to_char(access_time,'yyyy-mm-dd hh24:mi:ss')) as last_access_time from fample_user_login_statistics " +
                "group by user_name ";
        MapSqlParameterSource maps = new MapSqlParameterSource();
        maps.addValue("usernames", userNames);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.oracleJdbcTemplate.getDataSource());
        List<ReportUser> res  = template.query(sql,maps,rs ->{
            List<ReportUser> ReportUsers = new LinkedList<>();
            while (rs.next()) {
                ReportUser reportUser = new ReportUser(rs.getString("user_name"),rs.getString("total_user"),rs.getTimestamp("last_access_time"));
                ReportUsers.add(reportUser);
            }
            return ReportUsers;
        });
        return res;
    }

    @Autowired
    private JdbcTemplate oracleJdbcTemplate;

}
