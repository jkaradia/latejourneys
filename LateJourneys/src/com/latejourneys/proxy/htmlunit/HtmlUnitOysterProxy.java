package com.latejourneys.proxy.htmlunit;

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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.latejourneys.domain.Account;
import com.latejourneys.domain.Card;
import com.latejourneys.domain.Journey;
import com.latejourneys.util.TimeUtil;

@SuppressWarnings("serial")
@Component
public class HtmlUnitOysterProxy implements Serializable {
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private HtmlUnitWebClient webClient;

	@Autowired
	private TimeUtil timeUtil;
	

	final private static int TWO_WEEKS_EARLIER_IN_DAYS = -14;
	final private static int NOW_IN_DAYS = 0;

	Pattern DATE_PATTERN = Pattern
			.compile("(\\d{2}:\\d{2})\\s?-\\s?(\\d{2}:\\d{2})");

	Pattern STATION_PATTERN = Pattern.compile("([\\w\\s]*?) to ([\\w\\s]*?)");

	public HtmlPage getLogonPage() {
		HtmlPage page = webClient
				.getPage("https://oyster.tfl.gov.uk/oyster/entry.do");
		webClient.setCurrentPage(page);
		return page;
	}

	public HtmlPage authenticate(String userName, String password) {

		getLogonPage();
		HtmlForm form = (HtmlForm) webClient.getCurrentPage().getFirstByXPath(
				"//form[@id='sign-in']");
		HtmlInput input1 = form.getInputByName("UserName");
		input1.setValueAttribute(userName);

		HtmlInput input2 = form.getInputByName("Password");
		input2.setValueAttribute(password);

		HtmlPage page = webClient.clickByName(form, "Sign in");
		webClient.setCurrentPage(page);

		return page;

	}

	public Collection<Card> getCards() {

		HtmlPage page = webClient.getCurrentPage();

		HtmlElement result = (HtmlElement) page
				.getElementById("select_card_no");
		Collection<Card> cards = new ArrayList<Card>();

		for (DomElement element : result.getChildElements()) {
			if (NumberUtils.isNumber(element.asText())) {
				Card card = new Card();
				card.setCardNumber(element.asText());
				cards.add(card);
			}
		}
		return cards;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.latejourneys.proxy.htmlunit.OysterProxy#getJourneys(java.lang.String)
	 */
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
		calendar.add(Calendar.DATE, NO_OF_DAYS);
		Date twoWeeksEarlierDate = calendar.getTime();
		String formattedTwoWeeksEalierDate = df.format(twoWeeksEarlierDate);

		SimpleDateFormat format = new SimpleDateFormat(
				"EEEE, dd MMMM yyyy/HH:mm");

		Pattern station = Pattern.compile("([\\w\\s]*?) to ([\\w\\s]*?)");

		HtmlForm form = (HtmlForm) page
				.getFirstByXPath("//form[@id='selectCardForm']");

		webClient.select(form, "select_card_no", cardNumber);

		final HtmlPage page2 = webClient.clickByValue(form, "Go");

		HtmlPage page3 = webClient.getPageByAnchor(page2, "Journey history");

		HtmlTable table = page3
				.getFirstByXPath("//table[@class='journeyhistory']");

		HtmlForm form2 = (HtmlForm) table
				.getFirstByXPath("//form[@name='dateRangeForm']");

		webClient.select(form2, "date-range", "custom date range");

		webClient.setValue(form2, "csDateFrom", formattedTwoWeeksEalierDate);

		webClient.setValue(form2, "csDateTo", formattedCurrentDate);

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
								System.out.println("Start: " + start + " End: "
										+ end);
								System.out
										.println(cell.getTextContent().trim());
								throw new RuntimeException(e);
							}

						}
					}
				} else if (classAttribute.equals("status-1")) {

					Matcher m = station.matcher(cell.getTextContent().trim());
					if (m.matches()) {
						String fromStation = m.group(1);
						String toStation = m.group(2);

						j.setFromStation(fromStation);
						j.setToStation(toStation);
						journeys.add(j);
					}
				} else if (classAttribute.equals("day-date status-1")) {

					if (cell.getChildElementCount() > 0) {
						Iterable<DomElement> i = cell.getChildElements();
						DomElement anchor = i.iterator().next();
						currentDay = anchor.getTextContent().trim();

					} else {
						currentDay = cell.getTextContent().trim();
					}
					j = new Journey();

				}

			}

		}

		System.out.println("Finished: "
				+ (System.currentTimeMillis() - startTime));
		return journeys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.latejourneys.proxy.htmlunit.OysterProxy#getAccountDetails()
	 */
	public Account getAccountDetails() {

		long startTime = System.currentTimeMillis();

		HtmlPage page = webClient.getCurrentPage();

		HtmlPage page2 = webClient.getPageByAnchor(page, "My account");

		HtmlPage page3 = webClient.getPageByAnchor(page2, "Change details");

		Account account = new Account();

		// TODO Title????
		account.setForename(webClient.getDefaultInputValue(page3, "firstName"));
		account.setMiddle(webClient.getDefaultInputValue(page3, "middle"));
		account.setSurname(webClient.getDefaultInputValue(page3, "surname"));
		account.setHouseName(webClient.getDefaultInputValue(page3, "houseName"));
		account.setHouseNumber(webClient.getDefaultInputValue(page3,
				"houseNumber"));
		account.setStreet(webClient.getDefaultInputValue(page3, "street"));
		account.setTown(webClient.getDefaultInputValue(page3, "town"));
		account.setCounty(webClient.getDefaultInputValue(page3, "county"));
		account.setPostcode(webClient.getDefaultInputValue(page3, "postcode"));
		account.setHomephone(webClient.getDefaultInputValue(page3, "phone"));
		account.setAltPhone(webClient.getDefaultInputValue(page3, "altPhone"));
		account.setEmail(webClient.getDefaultInputValue(page3, "email"));

		System.out.println("Account Time: "
				+ (System.currentTimeMillis() - startTime));
		return account;
	}

	public Card getCardDetails(String cardNumber) {

		HtmlPage page = webClient.getCurrentPage();

		Card card = context.getBean(Card.class);
		card.setJourneys(new ArrayList<Journey>());
		
		card.setCardNumber(cardNumber);
		
		if (page == null
				|| !"Oyster online - Transport for London - Card overview"
						.equals(page.getTitleText())) {
			authenticate("jayesh@karadia.com", "Pa55word");
		}

		HtmlForm form = (HtmlForm) page
				.getFirstByXPath("//form[@id='selectCardForm']");

		webClient.select(form, "select_card_no", cardNumber);

	    webClient.getOptions().setJavaScriptEnabled(true);
		final HtmlPage page2 = webClient.clickByValue(form, "Go");

		HtmlPage page3 = webClient.getPageByAnchor(page2, "Journey history");

		HtmlTable table = page3
				.getFirstByXPath("//table[@class='journeyhistory']");

		HtmlForm form2 = (HtmlForm) table
				.getFirstByXPath("//form[@name='dateRangeForm']");

		webClient.select(form2, "date-range", "custom date range");

		webClient.setValue(form2, "csDateFrom",
				timeUtil.getOffsetDate(TWO_WEEKS_EARLIER_IN_DAYS));

		webClient.setValue(form2, "csDateTo",
				timeUtil.getOffsetDate(NOW_IN_DAYS));
		HtmlPage page4 = webClient.clickByValue(form2, "Go");
		HtmlTable table2 = page4
				.getFirstByXPath("//table[@class='journeyhistory']");

		int difference = 0;
		for (HtmlTableBody body : table2.getBodies()) {

			for (HtmlTableRow row : body.getRows()) {
				for (HtmlTableCell cell : row.getCells()) {
					String from, to;
					String classAttribute = cell.getAttribute("class");
					if (classAttribute.equals("no-wrap")) {

						Matcher m = DATE_PATTERN.matcher(cell.getTextContent()
								.trim());
						if (m.matches()) {
							difference = timeUtil.timeDifference(m.group(1)
									.trim(), m.group(2).trim());
						}
					} else if (classAttribute.equals("status-1")) {

						Matcher m = STATION_PATTERN.matcher(cell
								.getTextContent().trim());
						if (m.matches()) {
							Journey journey = context.getBean(Journey.class);
						 
							from = m.group(1);
							to = m.group(2);
							System.out.println(difference + "   From: " + from
									+ " To " + to);

							System.out.println("From: " + from + " To: " + to);
							
							journey.setFromStation(from);
							journey.setToStation(to);
						//	journey.setStartTime(startTime)
							card.getJourneys().add(journey);
							

						}
					} else if (classAttribute.equals("day-date status-1")) {
						
						System.out.println("Day " + cell.getTextContent().trim());
					}

				}

			}
		}

		return card;

	}

}