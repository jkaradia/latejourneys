package com.latejourneys.domain;

import java.util.ArrayList;
import java.util.Collection;

public class AllCards {

	public Collection<Card> cards = new ArrayList<Card>();

	public void addCard(Card card) {
		cards.add(card);
	}

	public Collection<Card> getCards() {
		return cards;
	}
	
	public void setCards(Collection<Card> cards) {
		this.cards = cards;
	}
}
