package com.latejourneys.web;

import java.util.Collection; 

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.latejourneys.domain.Account; 
import com.latejourneys.domain.Card;
import com.latejourneys.domain.Journey;
import com.latejourneys.service.DirectionService;
import com.latejourneys.service.OysterService;

/**
 * Handles and retrieves the main requests
 */
@Controller
// @RequestMapping("/authenticate")
@Scope("request")
public class HomeController {

	@Autowired
	private OysterService oysterService;
	
	@Autowired
	private DirectionService directionService;

	/**
	 * Handles request for adding two numbers
	 */
	@RequestMapping(value = "/cards", method = RequestMethod.GET)
	public @ResponseBody
	Collection <Card> getAllCards() {
		long startTime = System.currentTimeMillis();
		Collection <Card> oysterContext = oysterService.getCards();

		System.out.println("******"+(System.currentTimeMillis()-startTime) + "*******");
		// Return JSON of allCards;
		return oysterContext;
	}
	
	/**
	 * Handles request for adding two numbers
	 */
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public @ResponseBody
	Account getAccounts() { 
		Account account = oysterService.getAccount();
 
		// Return JSON of allCards;
		return account;
	}
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public 
	String authenticate(
			@RequestParam(value = "j_username", required = true) String userName,
			@RequestParam(value = "j_password", required = true) String password ) {

		 
		  oysterService.authenticate (userName, password);
 
		 
		// This will resolve to /WEB-INF/jsp/nonajax-add-result-page.jsp
		return "display_cards";
	}

	@RequestMapping(value = "/duration/{from}", method = RequestMethod.GET)
	public @ResponseBody 
	Integer duration(
			@PathVariable(value = "from") String from,
			@RequestParam(value = "to", required = true) String to ) {

		 
		  return directionService.duration (from.replace('+',' '), to);
	}
}
