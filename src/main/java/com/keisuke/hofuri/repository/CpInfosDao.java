package com.keisuke.hofuri.repository;

import com.keisuke.hofuri.entity.CpInfo;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class CpInfosDao {
  @Autowired
  NamedParameterJdbcTemplate jdbcTemplate;

  public int create(CpInfo cpInfo) {
    String sql = "INSERT INTO cp_infos VALUES (:id, :name, :isinCode, :bondUnit, :amount, :issureCode, :fetchedDate)";
    SqlParameterSource parameterSource = new MapSqlParameterSource("id", null)
                                             .addValue("name", cpInfo.getName())
                                             .addValue("isinCode", cpInfo.getIsinCode())
                                             .addValue("bondUnit", cpInfo.getBondUnit())
                                             .addValue("amount", cpInfo.getAmount())
                                             .addValue("issureCode", cpInfo.getIssuerCode())
                                             .addValue("fetchedDate", cpInfo.getFetchedDate());
    return jdbcTemplate.update(sql, parameterSource);
  }

  /**
   * 入力した日付の残高がすでに取得されているかチェックします。
   * @param fetchedDate チェックする日付
   * @return　取得済みであればtrue
   */
  public boolean isFetched(Date fetchedDate) {
    String sql = "SELECT COUNT(id) FROM cp_infos WHERE fetched_date = :fetchedDate LIMIT 1";
    SqlParameterSource parameterSource = new MapSqlParameterSource("fetchedDate", fetchedDate);
    return jdbcTemplate.queryForObject(sql, parameterSource, Integer.class) != 0 ? true : false;
  }
}
