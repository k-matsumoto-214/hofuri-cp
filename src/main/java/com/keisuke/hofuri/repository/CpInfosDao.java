package com.keisuke.hofuri.repository;

import com.keisuke.hofuri.entity.CpInfo;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class CpInfosDao {
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  public int create(CpInfo cpInfo) {
    String sql = "INSERT INTO cp_infos VALUES (:id, :name, :isinCode, :bondUnit, :amount, :issureCode, :fetchedDate)";
    SqlParameterSource parameterSource = new MapSqlParameterSource("id", null)
                                             .addValue("name", cpInfo.getName())
                                             .addValue("isinCode", cpInfo.getIsinCode())
                                             .addValue("bondUnit", cpInfo.getBondUnit())
                                             .addValue("amount", cpInfo.getAmount())
                                             .addValue("issureCode", cpInfo.getIssureCode())
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

  /**
   * 発行者コードから日ごとの発行残高を取得します。
   * @param issureCode 発行者コード
   * @return 6/15以降の各営業日における発行残高 *残高が0の日はnullが入ります。
   */
  public List<Integer> fetchDailyAmounts(String issureCode) {
    String sql = "SELECT grouped.amount "
                 + "FROM (SELECT SUM(amount) AS amount, fetched_date FROM cp_infos where issure_code = :issureCode GROUP BY fetched_date) AS grouped "
                 + "RIGHT JOIN workdays ON grouped.fetched_date = workdays.workday "
                 + "WHERE workdays.fetched_flg = true";
    SqlParameterSource parameterSource = new MapSqlParameterSource("issureCode", issureCode);
    return jdbcTemplate.queryForList(sql, parameterSource, Integer.class);
  }

  /**
   * DBに登録されている発行者コードをすべて取得します。
   * @return 発行者コードの配列
   */
  public List<String> fetchIssureCodes() {
    String sql = "SELECT issure_code FROM cp_infos GROUP BY issure_code ORDER BY issure_code ASC";
    SqlParameterSource parameterSource = null;
    return jdbcTemplate.queryForList(sql, parameterSource, String.class);
  }

  /**
   * 発行者コードから発行者名を取得します。
   * @param issureCode
   * @return 発行者名、発行者コードを持つCpInfoオブジェクト（ほかの値はnull)
   */
  public CpInfo fetchIssureData(String issureCode) {
    String sql = "SELECT name, issure_code FROM cp_infos WHERE issure_code = :issureCode LIMIT 1";
    SqlParameterSource parameterSource = new MapSqlParameterSource("issureCode", issureCode);
    RowMapper<CpInfo> rowMapper = new BeanPropertyRowMapper<CpInfo>(CpInfo.class);
    return jdbcTemplate.queryForObject(sql, parameterSource, rowMapper);
  }

  /**
   * 日ごとの総発行残高を取得します。
   * @return 6/15以降の各営業日における日ごとのCP発行残高 *残高が0の日はnullが入ります。
   */
  public List<Integer> fetchDailyTotalAmounts(Date updateDate) {
    String sql = "SELECT grouped.amount "
                 + "FROM (SELECT SUM(amount) AS amount, fetched_date FROM cp_infos GROUP BY fetched_date) AS grouped "
                 + "RIGHT JOIN workdays ON grouped.fetched_date = workdays.workday "
                 + "WHERE workdays.workday <= :updateDate";
    SqlParameterSource parameterSource = new MapSqlParameterSource("updateDate", updateDate);
    return jdbcTemplate.queryForList(sql, parameterSource, Integer.class);
  }

  /**
   * 最新日付における発行残高がある発行者数を取得します。
   * @return 最新日付における発行残高がある発行者数
   */
  public Integer countTodaysIssure(Date updateDate) {
    String sql = "SELECT COUNT(*) FROM (SELECT issure_code FROM cp_infos WHERE fetched_date = :updateDate GROUP BY issure_code) AS sub";
    SqlParameterSource parameterSource = new MapSqlParameterSource("updateDate", updateDate);
    return jdbcTemplate.queryForObject(sql, parameterSource, Integer.class);
  }

  /**
   * 最新日付における発行残高を取得します。
   * @return 残高がない場合はnullが返ります
   */
  public Integer fetchTodaysAmount(Date updateDate) {
    String sql = "SELECT SUM(amount) FROM cp_infos WHERE fetched_date = :updateDate";
    SqlParameterSource parameterSource = new MapSqlParameterSource("updateDate", updateDate);
    return jdbcTemplate.queryForObject(sql, parameterSource, Integer.class);
  }

  /**
   * 最新日付における発行残高Top10を取得します。
   * @param updateDate
   * @return 10件ない場合は10件未満のリストを返します。
   */
  public List<CpInfo> fetchTop10Isuures(Date updateDate) {
    String sql = "SELECT name, issure_code, SUM(amount) as amount FROM cp_infos "
                 + "WHERE fetched_date = :updateDate GROUP BY name, issure_code ORDER BY amount DESC LIMIT 10";
    SqlParameterSource parameterSource = new MapSqlParameterSource("updateDate", updateDate);
    RowMapper<CpInfo> rowMapper = new BeanPropertyRowMapper<CpInfo>(CpInfo.class);
    return jdbcTemplate.query(sql, parameterSource, rowMapper);
  }

  /**
   * 該当日時のデータを削除します
   * @param date 削除したい日付
   * @return 削除件数
   */
  public int deleteCpInfosByDate(Date date) {
    String sql = "DELETE from cp_infos WHERE fetched_date = :date";
    String sql2 = "UPDATE workdays SET fetched_flg = false WHERE workday = :date";
    SqlParameterSource parameterSource = new MapSqlParameterSource("date", date);
    SqlParameterSource parameterSource2 = new MapSqlParameterSource("date", date);
    
    jdbcTemplate.update(sql2, parameterSource2);
    return jdbcTemplate.update(sql, parameterSource);
  }
}
