package com.keisuke.hofuri.controller;

import com.keisuke.hofuri.service.CpService;
import java.util.List;
import java.util.Map;
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
  public String getCpList(Model model) throws InterruptedException {
    List<Map<String, Object>> cpList = cpService.fetchTodaysCpBalance();
    model.addAttribute("cpList", cpList);
    return "cp";
  }
}
