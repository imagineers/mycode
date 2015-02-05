package com.introp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserTaskController {

	@RequestMapping(value = "/addConfiguration", method = RequestMethod.GET)
	public String loginFailed(ModelMap model){
		return "addConfiguration";
	}
}
