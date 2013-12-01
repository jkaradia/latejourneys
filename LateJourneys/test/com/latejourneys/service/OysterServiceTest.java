package com.latejourneys.service;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.latejourneys.domain.Card;
import com.latejourneys.domain.Journey;   

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:LateJourneys-service.xml" })
public class OysterServiceTest {

	@Autowired
	private OysterService oyster;
	
	@Test
	public void testAuthenticate() { 

		oyster.authenticate("jayesh@karadia.com", "Pa55word");
	}

	@Test
	public void testGetCards() {
		 

		oyster.authenticate("jayesh@karadia.com", "Pa55word");
		Collection<Card> cards = oyster.getCards();

		Collection<String> cardNumbers = new ArrayList<String>();
		for (Card card : cards) {
			System.out.println(card.getCardNumber());
			cardNumbers.add(card.getCardNumber());
		}
		
		Assert.assertTrue(cardNumbers.contains("054889018917"));
		Assert.assertTrue(cardNumbers.contains("056408182999"));
		Assert.assertTrue(cardNumbers.contains("056426184411"));
		Assert.assertTrue(cardNumbers.contains("057435651554"));

		
		Assert.assertEquals(4, cards.size());
	}
	
	@Test
	public void testGetCardDetails() {
		 

		oyster.authenticate("jayesh@karadia.com", "Pa55word");
		Card card = oyster.getCardDetails("057435651554");
		
		Assert.assertEquals("057435651554", card.getCardNumber());
		 
	}



	

}
