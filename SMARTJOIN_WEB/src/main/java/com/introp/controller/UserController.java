package com.introp.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

	@RequestMapping(value = "/addUser", method = RequestMethod.GET)
	public String addUser(Model model){
		return "addUser";
	}
	
	
	
}
