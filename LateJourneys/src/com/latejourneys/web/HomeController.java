package com.latejourneys.web;

import java.util.Collection; 

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.latejourneys.domain.Account;
import com.latejourneys.domain.AllCards;
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

	@Resource(name = "oysterService")
	private OysterService oysterService;
	
	@Resource(name = "directionService")
	private DirectionService directionService;

	/**
	 * Handles request for adding two numbers
	 */
	@RequestMapping(value = "/cardnumbers", method = RequestMethod.GET)
	public @ResponseBody
	Collection<String> getCardNumbers() {

		 
		Collection<String> cards = oysterService.getCardNumbers();

		// Return JSON of OyesterContext;
		return cards;
	}
	
	/**
	 * Handles request for adding two numbers
	 */
	@RequestMapping(value = "/card/{cardnumber}", method = RequestMethod.GET)
	public @ResponseBody
	Collection<Journey> getJourneys(@PathVariable(value = "cardnumber") String cardNumber) {

		 
		Collection<Journey> journeys = oysterService.getJourneys(cardNumber);

		// Return JSON of OyesterContext;
		return journeys;
	}
	

	/**
	 * Handles request for adding two numbers
	 */
	@RequestMapping(value = "/cards", method = RequestMethod.GET)
	public @ResponseBody
	AllCards getAllCards() {
		long startTime = System.currentTimeMillis();
		AllCards oysterContext = oysterService.allCards();

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
	
	@RequestMapping(value = "/page/{pagename}", method = RequestMethod.GET)
	public @ResponseBody 
	Boolean prepareOysterHomePage(
			@PathVariable(value = "pagename") String pageName  ) {
		  return oysterService.preparePage(pageName);
	}
}
