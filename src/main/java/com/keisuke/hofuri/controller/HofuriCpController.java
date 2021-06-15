package com.keisuke.hofuri.controller;

import com.keisuke.hofuri.entity.CpInfo;
import com.keisuke.hofuri.service.CpService;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HofuriCpController {
  @Autowired
  CpService cpService;

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("test", "hello hofuri!!");
    return "index";
  }

  @GetMapping("/get-cp-list")
  public String getCpList(Model model) throws InterruptedException, ParseException {
    List<CpInfo> cpList = cpService.fetchTodaysCpBalance();
    model.addAttribute("cpList", cpList);
    return "cp";
  }
}
