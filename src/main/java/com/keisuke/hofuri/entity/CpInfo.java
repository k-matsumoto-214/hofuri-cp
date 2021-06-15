package com.keisuke.hofuri.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cp_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CpInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "name")
  private String name;
  @Column(name = "isin_code")
  private String isinCode;
  @Column(name = "bond_unit")
  private int bondUnit;
  @Column(name = "amount")
  private int amount;
  @Column(name = "issure_code")
  private String issuerCode;
}
