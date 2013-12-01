package com.latejourneys.service;

import java.util.logging.Level;
import java.util.logging.Logger;

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
		HtmlInput input1 = form3.getInputByName("j_username");
		input1.setValueAttribute("jkaradia");

		HtmlInput input2 = form3.getInputByName("j_password");
		input2.setValueAttribute("Jay50esh");

		HtmlSubmitInput submit1 = form3.getInputByName("Sign in");

		page1 = submit1.click();
		
		HtmlPage page2 = webClient
				.getPageByAnchor(page1, "My Oyster details");
		
		HtmlPage page3 = webClient
				.getPageByAnchor(page2, "Change details");
		
		HtmlElement result = (HtmlElement) page3
				.getElementById("orderdiv");
		
		HtmlSelect title = result.getFirstByXPath("//select[@name='title']");
		System.out.println("Title " + title.getDefaultValue());
	 

		HtmlInput firstName = result.getFirstByXPath("//input[@id='forename']");
		System.out.println("First Name:" + firstName.getValueAttribute());
		
		HtmlInput initial = result.getFirstByXPath("//input[@id='middle_initial']");
		System.out.println("Initial:" + initial.getValueAttribute());
		
		HtmlInput surname = result.getFirstByXPath("//input[@id='surname']");
		System.out.println("Surname:" + surname.getValueAttribute());
		
		HtmlInput housename = result.getFirstByXPath("//input[@id='housename']");
		System.out.println("Housename:" + housename.getValueAttribute());
		
		HtmlInput housenumber = result.getFirstByXPath("//input[@id='housenumber']");
		System.out.println("Number:" + housenumber.getValueAttribute());
		
		HtmlInput street = result.getFirstByXPath("//input[@id='street']");
		System.out.println("Street:" + street.getValueAttribute());
		
		HtmlInput town = result.getFirstByXPath("//input[@id='town']");
		System.out.println("Street:" + town.getValueAttribute());
		
		HtmlInput county = result.getFirstByXPath("//input[@id='county']");
		System.out.println("Street:" + county.getValueAttribute());
		
		HtmlInput homephone = result.getFirstByXPath("//input[@id='homephone']");
		System.out.println("Home:" + homephone.getValueAttribute());
		
		HtmlInput mobile = result.getFirstByXPath("//input[@id='mobilephone']");
		System.out.println("Mobile" + mobile.getValueAttribute());
		
		HtmlInput email = result.getFirstByXPath("//input[@id='email']");
		System.out.println("email" + email.getValueAttribute());
		
		
		webClient.closeAllWindows();

	}


}
