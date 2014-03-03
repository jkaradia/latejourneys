package com.latejourneys.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "classpath:root-context.xml" })
public class DirectionTest extends DefaultHandler {

	final private String charset = "UTF-8";

	public InputStream connect() throws Exception {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"212.54.128.40", 3128));

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

	static	String from = "Rayners Lane";
	static	String to = "Great Portland Street";
	
	static int min = Integer.MAX_VALUE;
	static int max = 0;
	static int journeys = 0;

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

	@Test
	public void testFileParse() {
		DirectionTest readXml = new DirectionTest();
		readXml.getXml();
		
		System.out.println("Total Journey: " + journeys);
		System.out.println("From: " + from + " To: " + to);
		System.out.println("Range: " + min + " min to " + max + " min");
	}
}
