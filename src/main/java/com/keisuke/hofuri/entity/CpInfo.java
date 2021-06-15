package com.keisuke.hofuri.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.Valid;
import lombok.Data;

@Entity
@Data
public class CpInfo {
  @Id
  @GeneratedValue
  @Column(name = "company_id")
  private int id;
  private String name;
  @Valid
  private String isinCode;
  private int bondUnit;
  private int amount;
  private String issuerCode;
}
