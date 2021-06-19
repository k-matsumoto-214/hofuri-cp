package com.keisuke.hofuri.repository;

import com.keisuke.hofuri.entity.Workday;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class WorkdaysDao {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  public List<Date> fetchWorkdays(Date updateDate) {
    String sql = "SELECT workday FROM workdays WHERE workday <= :updateDate";
    SqlParameterSource parameterSource = new MapSqlParameterSource("updateDate", updateDate);
    return jdbcTemplate.queryForList(sql, parameterSource, Date.class);
  }

  public Date fetchUpdatDate() {
    String sql = "SELECT MAX(workday) FROM workdays WHERE fetched_flg = true";
    SqlParameterSource parameterSource = null;
    return jdbcTemplate.queryForObject(sql, parameterSource, Date.class);
  }

  public int update(Workday workday) {
    String sql = "UPDATE workdays SET fetched_flg = :fetchedFlg WHERE workday = :workday";
    SqlParameterSource parameterSource = new MapSqlParameterSource("fetchedFlg", workday.isFetchedFlg())
                                             .addValue("workday", workday.getWorkday());
    return jdbcTemplate.update(sql, parameterSource);
  }
}
