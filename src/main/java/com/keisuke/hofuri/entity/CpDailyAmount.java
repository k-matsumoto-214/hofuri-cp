package com.keisuke.hofuri.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 各発行体の日ごとの発行量を表示するためのオブジェクト
public class CpDailyAmount {
  private String issureCode;
  private String name;
  private List<Integer> dailyAmounts;
}