package com.latejourneys.service.oysterparsing;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.latejourneys.domain.Account;
import com.latejourneys.domain.Card;
import com.latejourneys.domain.Journey;
import com.latejourneys.proxy.htmlunit.HtmlUnitOysterProxy;
import com.latejourneys.service.OysterService;

@Service("oysterService")
public class HtmlUnitOysterService implements OysterService {

	@Autowired
	private HtmlUnitOysterProxy oysterProxy;
 

	/**
	 * @param oysterProxy the oysterProxy to set
	 */

	public void setOysterProxy(HtmlUnitOysterProxy oysterProxy) {
		this.oysterProxy = oysterProxy;
	}
 

 
	public Collection<Card> getCards() {
 		return oysterProxy.getCards();	 
	}

	 
	public void authenticate(String userName, String password) {
		oysterProxy.authenticate(userName, password);

	}
	
	 
	public void initialise( ) {
		oysterProxy.getLogonPage(); 

	}
	
	 
	
	
	 
	public Account getAccount() {
		return oysterProxy.getAccountDetails();
	}



	@Override
	public Card getCardDetails(String cardNumber) {
		return oysterProxy.getCardDetails(cardNumber);
	}



	@Override
	public void close() {
		oysterProxy.close();
		
	}
	
	

}