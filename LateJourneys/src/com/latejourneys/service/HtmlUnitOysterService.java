package com.latejourneys.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.latejourneys.domain.Account;
import com.latejourneys.domain.AllCards;
import com.latejourneys.domain.Card;
import com.latejourneys.domain.Journey;
import com.latejourneys.proxy.OysterProxy;

@Service("oysterService")
@Scope("request")
public class HtmlUnitOysterService implements OysterService {

	@Autowired
	private OysterProxy oysterProxy;
 

	/**
	 * @param oysterProxy the oysterProxy to set
	 */

	public void setOysterProxy(OysterProxy oysterProxy) {
		this.oysterProxy = oysterProxy;
	}

 
	public Collection<String> getCardNumbers() {
		return oysterProxy.getCards();
	}

 
	public AllCards allCards() {

		AllCards context = new AllCards();
		Collection<String> cardNumbers = oysterProxy.getCards();
		for (String cardNumber : cardNumbers) {
			Card card = new Card();
			card.setCardNumber(cardNumber); 
			card.setJourneys(getJourneys(cardNumber));
			context.addCard(card);
		}

		return context;

	}

	 
	public void authenticate(String userName, String password) {
		oysterProxy.authenticate(userName, password);

	}
	
	 
	public boolean preparePage(String page) {
		oysterProxy.getLoginForm();
		return true;

	}
	
	 
	public Collection<Journey> getJourneys(String cardNumber) {
		return oysterProxy.getJourneys(cardNumber);
	}
	
	 
	public Account getAccount() {
		return oysterProxy.getAccountDetails();
	}
	
	

}