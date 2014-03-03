package com.latejourneys.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.latejourneys.proxy.htmlunit.HtmlUnitWebClient;

public class GetDetailsTest {

	

	@Test
	public void testGetDetails() throws Exception {
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.OFF);
	    
		final HtmlUnitWebClient webClient = new HtmlUnitWebClient();

		HtmlPage page1 = webClient
				.getPage("https://oyster.tfl.gov.uk/oyster/entry.do");

		HtmlForm form3 = (HtmlForm) page1
				.getFirstByXPath("//form[@id='sign-in']");
		HtmlInput input1 = form3.getInputByName("UserName");
		input1.setValueAttribute("jayesh@karadia.com");

		HtmlInput input2 = form3.getInputByName("Password");
		input2.setValueAttribute("Pa55word");

		HtmlSubmitInput submit1 = form3.getInputByName("Sign in");

		page1 = submit1.click();
		
		HtmlPage page2 = webClient
				.getPageByAnchor(page1, "My Oyster details");
		
		HtmlPage page3 = webClient
				.getPageByAnchor(page2, "Change details");
		
		HtmlElement result = (HtmlElement) page3
				.getElementById("orderdiv");
		
		HtmlSelect title = result.getFirstByXPath("//select[@name='title']");
		Assert.assertEquals("Mr", title.getDefaultValue());
	 

		HtmlInput firstName = result.getFirstByXPath("//input[@id='forename']");
		Assert.assertEquals("Jayesh",firstName.getValueAttribute());
		
		HtmlInput initial = result.getFirstByXPath("//input[@id='middle_initial']");
		Assert.assertEquals("P", initial.getValueAttribute());
		
		HtmlInput surname = result.getFirstByXPath("//input[@id='surname']");
		Assert.assertEquals("Karadia", surname.getValueAttribute());
		
		HtmlInput housename = result.getFirstByXPath("//input[@id='housename']");
		Assert.assertEquals("",housename.getValueAttribute());
		
		HtmlInput housenumber = result.getFirstByXPath("//input[@id='housenumber']");
		Assert.assertEquals("7", housenumber.getValueAttribute());
		
		HtmlInput street = result.getFirstByXPath("//input[@id='street']");
		Assert.assertEquals("Carlise Close",street.getValueAttribute());
		
		HtmlInput town = result.getFirstByXPath("//input[@id='town']");
		Assert.assertEquals("PINNER", town.getValueAttribute());
		
		HtmlInput county = result.getFirstByXPath("//input[@id='county']");
		Assert.assertEquals("Middlesex",county.getValueAttribute());
		
		HtmlInput homephone = result.getFirstByXPath("//input[@id='homephone']");
		Assert.assertEquals("07973626518", homephone.getValueAttribute());
		
		HtmlInput mobile = result.getFirstByXPath("//input[@id='mobilephone']");
		Assert.assertEquals("", mobile.getValueAttribute());
		
		HtmlInput email = result.getFirstByXPath("//input[@id='email']");
		Assert.assertEquals("jayesh@karadia.com", email.getValueAttribute());
		
		
		webClient.closeAllWindows();

	}


}
