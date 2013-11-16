package com.latejourneys.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class Card implements Serializable {


	 
	private static final long serialVersionUID = -7580518427832622322L;
	
	private String cardNumber;
	private Long balanceInPence;
	private Collection<Journey> journeys;
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public Long getBalanceInPence() {
		return balanceInPence;
	}
	public void setBalanceInPence(Long balanceInPence) {
		this.balanceInPence = balanceInPence;
	}
	public Collection<Journey> getJourneys() {
		return journeys;
	}
	public void setJourneys(Collection<Journey> collection) {
		this.journeys = collection;
	}
	
}
