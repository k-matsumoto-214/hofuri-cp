package com.keisuke.hofuri.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workday {
  private Date workday;
  private boolean fetchedFlg;
}
