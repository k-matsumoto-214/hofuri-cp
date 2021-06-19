package com.keisuke.hofuri.controller;

import com.keisuke.hofuri.entity.CpInfo;
import com.keisuke.hofuri.service.CpService;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HofuriCpController {
  @Autowired
  CpService cpService;

  @GetMapping("/")
  public String index(Model model) throws Exception {
    List<Date> workdays = cpService.fetchWorkdays();
    List<Integer> cpDailyTotalAmounts = cpService.fetchDailyTotalAmounts();
    model.addAttribute("updateDate", cpService.fetchUpdateDate());
    model.addAttribute("todaysIssures", cpService.countTodaysIssure());
    model.addAttribute("cpDailyTotalAmountGraphData", cpService.createCpDailyTotalAmountGraphData(workdays, cpDailyTotalAmounts));
    return "index";
  }

  @GetMapping("/get-cp-list")
  public String getCpList(Model model) throws Exception {
    WebDriver driver = cpService.getChoromDriver();
    List<CpInfo> cpInfos = cpService.fetchTodaysCpBalance(driver);
    cpService.registerCpInfos(cpInfos);
    model.addAttribute("message", "残高を取得しました。");
    System.out.println("終了しました。");
    return "index";
  }

  @GetMapping("/cp-list")
  public String fetchAllDailyAmounts(Model model) throws Exception {
    model.addAttribute("workdays", cpService.fetchWorkdays());
    model.addAttribute("cpDailyAmounts", cpService.fetchCpDailyAmounts());
    return "cp";
  }
}
