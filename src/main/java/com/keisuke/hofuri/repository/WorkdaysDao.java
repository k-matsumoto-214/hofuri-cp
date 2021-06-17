package com.keisuke.hofuri.repository;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class WorkdaysDao {
  @Autowired
  NamedParameterJdbcTemplate jdbcTemplate;

  public List<Date> fetchWorkdays() {
    String sql = "SELECT * FROM workdays WHERE workday < CURDATE()";
    SqlParameterSource parameterSource = null;
    return jdbcTemplate.queryForList(sql, parameterSource, Date.class);
  }
}
