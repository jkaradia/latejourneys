package com.latejourneys.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

public class LateJourneysEndToEndTest {



	final private String charset = "UTF-8";

	public InputStream connect() throws Exception {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"54.235.176.169", 3128));

		String query = String
				.format("language=en&sessionID=0&place_origin=London&type_origin=stop&name_origin=%s&place_destination=London&type_destination=stop&name_destination=%s&itdDate=20131101&itdTime=0800",

				URLEncoder.encode(from, charset),
						URLEncoder.encode(to, charset));

		String domain = "http://jpapi.tfl.gov.uk/api/XML_TRIP_REQUEST2";

		URL url = new URL(domain + '?' + query);

		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection(proxy);
		connection.setRequestProperty("Accept-Charset", charset);
		return connection.getInputStream();

	}
	
	//	String from = "Eastcote";
		// String to = "Elephant & Castle";

		String from = "Rayners Lane";
		String to = "Great Portland Street";
	
	 int min = Integer.MAX_VALUE;
	 int max = 0;
	 int journeys = 0;

	public void getXml() {
		try {
			// obtain and configure a SAX based parser
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

			// obtain object for SAX parser
			SAXParser saxParser = saxParserFactory.newSAXParser();

			// default handler for SAX handler class
			// all three methods are written in handler's body
			DefaultHandler defaultHandler = new DefaultHandler() {
				
				
				// this method is called every time the parser gets an open tag
				// '<'
				// identifies which tag is being open at time by assigning an
				// open flag
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {

				
					if (qName.equalsIgnoreCase("itdRoute")) {
						String[] duration = attributes
								.getValue("publicDuration").split(":");
						int durationInMinutes = Integer.valueOf(duration[0]) * 60 + Integer.valueOf(duration[1]);
						if (durationInMinutes > max) { 
							max = durationInMinutes;
						}
						
						if (durationInMinutes < min) { 
							min = durationInMinutes;
						}
						
						journeys++;
					//	System.out.println(durationInMinutes);
						
					}
				}

			};

			// parse the XML specified in the given path and uses supplied
			// handler to parse the document
			// this calls startElement(), endElement() and character() methods
			// accordingly

			InputStream is = getClass().getClassLoader().getResourceAsStream(
					"tooval.xml");

			is = connect();
			saxParser.parse(is, defaultHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testFileParse() {
		DirectionTest readXml = new DirectionTest();
		readXml.getXml();
		
		System.out.println("Total Journey: " + journeys);
		System.out.println("From: " + from + " To: " + to);
		System.out.println("Range: " + min + " min to " + max + " min");
	}
	
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
		HtmlInput input1 = form3.getInputByName("j_username");
		input1.setValueAttribute("jkaradia");

		HtmlInput input2 = form3.getInputByName("j_password");
		input2.setValueAttribute("Jay50esh");

		HtmlSubmitInput submit1 = form3.getInputByName("Sign in");

		page1 = submit1.click();

		HtmlElement result = (HtmlElement) page1
				.getElementById("select_card_no");
		for (DomElement element : result.getChildElements()) {
		String cardNumber = element.asText().trim();
		
			System.out.println("cardnumber:" + cardNumber);
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
								 from = m.group(1);
								 to = m.group(2);
								System.out.println(difference + "   From: " + from
										+ " To " + to);
								
								
								getXml();
								
								System.out.println("Total Journey: " + journeys);
								System.out.println("From: " + from + " To: " + to);
								System.out.println("Range: " + min + " min to " + max + " min");
								
								if (difference > max + 15) {
									System.out.println("*************Claim***********");
								} else {
								System.out.println("*************OK****************");
								}
								 
								
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
