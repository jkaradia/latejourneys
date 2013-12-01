package com.latejourneys.service;

import java.util.Collection;

import com.latejourneys.domain.Account;
import com.latejourneys.domain.Card;

public interface OysterService {
 
	public void authenticate(String userName, String password);
	public Collection<Card> getCards();
	public Account getAccount();
	public void initialise();
	public Card getCardDetails(String cardNumber);
	
	

}
