package com.keisuke.hofuri.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HofuriCpController {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("test", "hello hofuri!!");
		return "index";
	}
	
	@GetMapping("/get-cp-list")
	public String getCpList(Model model) {
		model.addAttribute("cpList", "newCpList");
		return "cp";
	}
}
