package com.latejourneys.web;
 

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
 
@Controller
public final class HelloWorldController {
 
	@RequestMapping(value = "/home",  method = RequestMethod.GET)
	public String HelloWorldGet() {
		return "MyView";
	}
	
	@RequestMapping(value = "/home",  method = RequestMethod.POST)
	public String HelloWorldPost() {
		return "MyView";
	}
} 