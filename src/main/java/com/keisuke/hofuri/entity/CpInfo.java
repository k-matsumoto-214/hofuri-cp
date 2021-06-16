package com.keisuke.hofuri.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpInfo {
  private Long id;
  private String name;
  private String isinCode;
  private int bondUnit;
  private int amount;
  private String issuerCode;
  private Date fetchedDate;
}
