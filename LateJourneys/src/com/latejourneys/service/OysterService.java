package com.latejourneys.service;

import java.util.Collection;

import com.latejourneys.domain.Account;
import com.latejourneys.domain.Journey;
import com.latejourneys.domain.AllCards;

public interface OysterService {

	public Collection<String> getCardNumbers();
	public void authenticate(String userName, String password);
	public AllCards allCards();
	public Collection<Journey> getJourneys(String cardNumber);
	public boolean preparePage(String page);
	public Account getAccount();
	
	

}
