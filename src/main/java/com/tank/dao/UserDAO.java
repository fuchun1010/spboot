package com.tank.dao;

import com.tank.message.Address;
import com.tank.message.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author fuchun
 */
@Repository
public class UserDAO {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public List<User> findAll() {
    final String sql = "select name ,job, location from full_sample_users";
    return jdbcTemplate.query(sql, ((rs, rowNum) -> {
      String name = rs.getString("name");
      String job = rs.getString("job");
      String location = rs.getString("location");
      return new User().setJob(job).setAddress(new Address().setLocation(location)).setName(name);
    }));
  }
}
