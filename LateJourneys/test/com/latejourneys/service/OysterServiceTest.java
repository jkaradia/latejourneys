package com.latejourneys.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.latejourneys.domain.Card;
import com.latejourneys.domain.Journey;    

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:LateJourneys-service.xml" })
public class OysterServiceTest {

	Logger log = LoggerFactory.getLogger(OysterServiceTest.class);
	
	@Autowired
	private OysterService oyster;
	 
	
	@Before
	public void authenticate() { 

		oyster.authenticate("jayesh@karadia.com", "Pa55word");
	}
	
	@After
	public void cleanUp() { 

		oyster.close();
	}

	@Test
	public void testGetCards() {
		 
 
		Collection<Card> cards = oyster.getCards();

		Collection<String> cardNumbers = new ArrayList<String>();
		for (Card card : cards) {
			log.info(card.getCardNumber());
			cardNumbers.add(card.getCardNumber());
		}
		
		Assert.assertTrue(cardNumbers.contains("054889018917"));
		Assert.assertTrue(cardNumbers.contains("056408182999"));
		Assert.assertTrue(cardNumbers.contains("056426184411"));
		Assert.assertTrue(cardNumbers.contains("057435651554"));

		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(writer, cards);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("boo", writer.toString());
		Assert.assertEquals(4, cards.size());
	}
	
	@Test
	public void testGetCardDetails() {
		 
 
		Card card = oyster.getCardDetails("057435651554");
		
		Assert.assertEquals("057435651554", card.getCardNumber());
		 
	}



	

}
