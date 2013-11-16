package com.latejourneys.service;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.latejourneys.domain.Account;
import com.latejourneys.domain.Journey;

@Component 
@Scope("session")
public class OysterProxy implements Serializable{

	@Autowired
	private OysterWebClient webClient;
	
	 private HtmlForm loginForm; 

	public HtmlPage authenticate(String userName, String password) {
 
		HtmlForm form3 = getLoginForm();
		webClient.setValue(form3, "j_username", userName);
		webClient.setValue(form3, "j_password", password);
		long startTime = System.currentTimeMillis();
	 
		HtmlPage page = webClient.clickByName(form3, "Sign in");
		System.out.println("After Click" + (System.currentTimeMillis() - startTime));
		webClient.setCurrentPage(page);

		return page;
	}

	public HtmlForm getLoginForm() {
 
		if (loginForm == null) {
		long startTime = System.currentTimeMillis();
		HtmlPage page = webClient
				.getPage("https://oyster.tfl.gov.uk/oyster/entry.do");
			loginForm =   (HtmlForm) page
				.getFirstByXPath("//form[@id='sign-in']");
			System.out.println("Getting Logging Page" + (System.currentTimeMillis() - startTime));
			
			
		}
		return loginForm;
	}
	
	
	

	public Collection<String> getCards() {

		HtmlPage page = webClient.getCurrentPage();

		HtmlForm form3 = (HtmlForm) page
				.getFirstByXPath("//form[@id='selectCardForm']");
		HtmlElement result = form3.getElementById("select_card_no");

		Collection<String> cards = new ArrayList<String>();

		for (DomElement element : result.getChildElements()) {
			if (NumberUtils.isNumber(element.asText())) {
				cards.add(element.asText());
			}
		}
		return cards;
	}

	public Collection<Journey> getJourneys(String cardNumber) {

		System.out.println("Start " + cardNumber);
		long startTime = System.currentTimeMillis();
		Collection<Journey> journeys = new ArrayList<Journey>();
		 
		HtmlPage page = webClient.getCurrentPage();
		Pattern dates = Pattern
				.compile("(\\d{2}:\\d{2})\\s?-\\s?(\\d{2}:\\d{2})");

		int NO_OF_DAYS = -14;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date currentDate = new Date();
		String formattedCurrentDate = df.format(currentDate);
		
		GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(currentDate);
	    calendar.add(Calendar.DATE, NO_OF_DAYS );
	    Date twoWeeksEarlierDate = calendar.getTime();
	    String formattedTwoWeeksEalierDate = df.format(twoWeeksEarlierDate);
	    
	    
	    SimpleDateFormat format =
	            new SimpleDateFormat("EEEE, dd MMMM yyyy/HH:mm");
		
		Pattern station = Pattern.compile("([\\w\\s]*?) to ([\\w\\s]*?)");
 
		HtmlForm form = (HtmlForm) page
				.getFirstByXPath("//form[@id='selectCardForm']");

		webClient.select(form,"select_card_no",cardNumber); 

		final HtmlPage page2 = webClient.clickByValue(form, "Go");

		HtmlPage page3 = webClient.getPageByAnchor(page2, "Journey history");

		HtmlTable table = page3
				.getFirstByXPath("//table[@class='journeyhistory']");

		HtmlForm form2 = (HtmlForm) table
				.getFirstByXPath("//form[@name='dateRangeForm']");

		webClient.select(form2, "date-range", "custom date range");

		webClient.setValue(form2, "csDateFrom",formattedTwoWeeksEalierDate);

		webClient.setValue(form2, "csDateTo",formattedCurrentDate);

		HtmlPage page4 = webClient.clickByValue(form2, "Go");

		HtmlTable table2 = page4
				.getFirstByXPath("//table[@class='journeyhistory']");
 
		String currentDay = "";
		Journey j = new Journey();
		for (HtmlTableRow row : table2.getRows()) {

			for (HtmlTableCell cell : row.getCells()) {

				String classAttribute = cell.getAttribute("class");
				if (classAttribute.equals("no-wrap")) {

					
	 
					{
					Matcher m = dates.matcher(cell.getTextContent().trim());
					if (m.matches()) {
					
						String start = currentDay + '/' + m.group(1).trim();
						String end = currentDay + '/' + m.group(2).trim();
						 
						 
						try {
							Date startTime1 = format.parse(start);
						
						Date endDate1 = format.parse(end);
						
						j.setStartTime(startTime1);
						j.setEndTime(endDate1);
						
						} catch (ParseException e) {
							System.out.println("Start: " + start+ " End: " + end);
							System.out.println(cell.getTextContent().trim());
							throw new RuntimeException(e);
						} 
						
						
						
					}
					}
				} else if (classAttribute.equals("status-1")) {

					Matcher m = station.matcher(cell.getTextContent().trim());
					if (m.matches()) {
						String fromStation = m.group(1);
						String toStation =  m.group(2);
						
						j.setFromStation(fromStation);
						j.setToStation(toStation);
						journeys.add(j);
					}
				} else if (classAttribute.equals("day-date status-1") ) { 
 
					 if (cell.getChildElementCount() > 0) {
						 Iterable<DomElement> i = cell.getChildElements();
						 DomElement anchor =  i.iterator().next();
					currentDay = anchor.getTextContent().trim(); 
					 
					 
					 } else {
							currentDay = cell.getTextContent().trim();  
					 } 
					 j = new Journey();
					
				}

			}

		}
 
		System.out.println("Finished: " + (System.currentTimeMillis() - startTime));
		return journeys;
	}


	public Account getAccountDetails() {
	 
		long startTime = System.currentTimeMillis(); 
		 
		HtmlPage page = webClient.getCurrentPage();
		
		HtmlPage page2 = webClient.getPageByAnchor(page, "My account");
		
		HtmlPage page3 = webClient.getPageByAnchor(page2, "Change details");
		
		Account account = new Account(); 
		
		//TODO Title????
		account.setForename(webClient.getDefaultInputValue(page3,"firstName"));
		account.setMiddle(webClient.getDefaultInputValue(page3,"middle"));
		account.setSurname(webClient.getDefaultInputValue(page3,"surname"));
		account.setHouseName(webClient.getDefaultInputValue(page3,"houseName"));
		account.setHouseNumber(webClient.getDefaultInputValue(page3,"houseNumber"));
		account.setStreet(webClient.getDefaultInputValue(page3,"street"));
		account.setTown(webClient.getDefaultInputValue(page3,"town"));
		account.setCounty(webClient.getDefaultInputValue(page3,"county"));
		account.setPostcode(webClient.getDefaultInputValue(page3,"postcode"));
		account.setHomephone(webClient.getDefaultInputValue(page3,"phone"));
		account.setAltPhone(webClient.getDefaultInputValue(page3,"altPhone"));
		account.setEmail(webClient.getDefaultInputValue(page3,"email"));
		
		
	
		System.out.println("Account Time: " + (System.currentTimeMillis() - startTime));
		return account;
	} 

}