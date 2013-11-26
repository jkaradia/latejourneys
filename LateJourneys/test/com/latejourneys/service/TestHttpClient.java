package com.latejourneys.service;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.latejourneys.proxy.OysterWebClient;

public class TestHttpClient {

	// @Test
	public void homePage() throws Exception {
		final WebClient webClient = new WebClient();
		final HtmlPage page = webClient
				.getPage("http://htmlunit.sourceforge.net");
		Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit",
				page.getTitleText());

		final String pageAsXml = page.asXml();
		Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));

		final String pageAsText = page.asText();
		Assert.assertTrue(pageAsText
				.contains("Support for the HTTP and HTTPS protocols"));

		webClient.closeAllWindows();
	}

	@Test
	public void oysterLogin() throws Exception {
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.OFF);

		int NO_OF_DAYS = -14;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date currentDate = new Date();
		String formattedCurrentDate = df.format(currentDate);
		
		GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(currentDate);
	    calendar.add(Calendar.DATE, NO_OF_DAYS );
	    Date twoWeeksEarlierDate = calendar.getTime();
	    String formattedTwoWeeksEalierDate = df.format(twoWeeksEarlierDate);
	    
		final OysterWebClient webClient = new OysterWebClient();
		
		Pattern dates = Pattern
				.compile("(\\d{2}:\\d{2})\\s?-\\s?(\\d{2}:\\d{2})");

		Pattern station = Pattern.compile("([\\w\\s]*?) to ([\\w\\s]*?)");

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

		HtmlElement result = (HtmlElement) page1
				.getElementById("select_card_no");
	//	for (DomElement element : result.getChildElements()) {
	//		String cardNumber = element.asText().trim();
		
		{
		String cardNumber = "057435651554";
			
			if (isNumeric(cardNumber)) {
				

				HtmlForm form = (HtmlForm) page1
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
				 
				int difference = 0;
				for (HtmlTableBody body : table2.getBodies()) {
					 				
				for (HtmlTableRow row : body.getRows()) {
					for (HtmlTableCell cell : row.getCells()) {
						String classAttribute = cell.getAttribute("class");
						if (classAttribute.equals("no-wrap")) {

							Matcher m = dates.matcher(cell.getTextContent()
									.trim());
							if (m.matches()) {
								difference = timeDifference(m.group(1).trim(), m
												.group(2).trim());
							}
						} else if (classAttribute.equals("status-1")) {

							Matcher m = station.matcher(cell.getTextContent()
									.trim());
							if (m.matches()) {
								System.out.println(difference + "   From: " + m.group(1)
										+ " To " + m.group(2));
							}
						} else if (classAttribute.equals("day-date status-1")) {
							System.out.println(cell.getTextContent().trim());
						}

					}

				}
			}}
		}
		webClient.closeAllWindows();

	}

	// http://maps.googleapis.com/maps/api/directions/json?origin=Rayners%20Lane,%20London&destination=Barbican,%20London&sensor=false&departure_time=1357839269&mode=transit

	// @Test
	public void testParseTimeAndStation() {
		String s = "08:50 - 09:10 	Rayners Lane to Acton Town 	 �2.20 	�10.35";

		Pattern p = Pattern
				.compile("(\\d{2}:\\d{2})\\s?-\\s?(\\d{2}:\\d{2})([\\w\\s]*?) to ([\\w\\s]*?)�.*");
		Matcher m = p.matcher(s);

		Assert.assertTrue(m.matches());
		if (m.matches()) {
			System.out.println("Matches");
			System.out.println("Group 1:" + m.group(1).trim());
			System.out.println("Group 2:" + m.group(2).trim());

			System.out.println("Difference is "
					+ timeDifference(m.group(1).trim(), m.group(2).trim()));
			System.out.println("Group 3:" + m.group(3).trim());
			System.out.println("Group 4:" + m.group(4).trim());
		} else
			System.out.println("Not matches");

	}

	public int timeDifference(String startTime, String endTime) {
		String[] startTime2 = startTime.split(":");
		String[] endTime2 = endTime.split(":");

		int startHour = Integer.parseInt(startTime2[0]);
		int startMinute = Integer.parseInt(startTime2[1]);

		int endHour = Integer.parseInt(endTime2[0]);
		int endMinute = Integer.parseInt(endTime2[1]);

		return (endHour - startHour) * 60 + (endMinute - startMinute);

	}

	// @Test
	public void testTimeDifference() {
		Assert.assertEquals(30, timeDifference("14:26", "14:56"));
		Assert.assertEquals(30, timeDifference("14:46", "15:16"));
	}

	public static boolean isNumeric(String str) {
		return str.matches("\\d+");
	}

}
