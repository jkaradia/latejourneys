package com.latejourneys.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput; 
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect; 
import com.latejourneys.proxy.htmlunit.HtmlUnitWebClient;

public class ClaimFormTest {

	@Test
	public void testSimpleClaim() throws Exception {
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.OFF);

		final HtmlUnitWebClient webClient = new HtmlUnitWebClient();

		HtmlPage page1 = webClient
				.getPage("https://www.tfl.gov.uk/tfl/tickets/refunds/tuberefund/refund.aspx?mode=oyster");

		HtmlForm form = (HtmlForm) page1.getFirstByXPath("//form[@id='aspnetForm']");

		HtmlSelect title = form.getSelectByName("ctl00$cphMain$ddl_Title");
		title.setSelectedAttribute("Mr", true);

		HtmlInput surname = form.getInputByName("ctl00$cphMain$txt_surname");
		surname.setValueAttribute("Mouse");

		HtmlInput forename = form.getInputByName("ctl00$cphMain$txt_firstname");
		forename.setValueAttribute("Michael");

		HtmlInput address1 = form.getInputByName("ctl00$cphMain$txt_address1");
		address1.setValueAttribute("Address One");

		HtmlInput address2 = form.getInputByName("ctl00$cphMain$txt_address2");
		address2.setValueAttribute("Address Two");

		HtmlInput address3 = form.getInputByName("ctl00$cphMain$txt_address3");
		address3.setValueAttribute("Address Three");
		
		HtmlInput postcode = form.getInputByName("ctl00$cphMain$txt_postcode");
		postcode.setValueAttribute("AB12 3DE");

		HtmlInput phone = form.getInputByName("ctl00$cphMain$txt_telephone");
		phone.setValueAttribute("01234 56789012");

		HtmlInput email = form.getInputByName("ctl00$cphMain$txt_email");
		email.setValueAttribute("email@email.com");

		HtmlSelect ticketType = form
				.getSelectByName("ctl00$cphMain$ddl_TicketType");
		ticketType.setDefaultValue("Pre pay");

		HtmlInput oysterNumber = form
				.getInputByName("ctl00$cphMain$txt_oyster_number");
		oysterNumber.setValueAttribute("050234637564");	

		HtmlRadioButtonInput cardType = (HtmlRadioButtonInput) form
				.getElementById("cphMain_rbl_oyster_cardtype_0");
		cardType.setChecked(true);

		HtmlSelect lineOfDelay = form
				.getSelectByName("ctl00$cphMain$lb_lineofdelay");
		lineOfDelay.setDefaultValue("Circle");

		HtmlSelect startStation = form
				.getSelectByName("ctl00$cphMain$lb_startstation");
		startStation.setDefaultValue("Aldgate");

		HtmlSelect endStation = form
				.getSelectByName("ctl00$cphMain$lb_endstation");
		endStation.setDefaultValue("Amersham");
if (false) {
		HtmlSelect delayStation = form
				.getSelectByName("ctl00$cphMain$lb_stationofdelay");
		delayStation.setDefaultValue("Amersham");
} else {
		HtmlSelect delayFrom = form
				.getSelectByName("ctl00$cphMain$lb_stationofdelay1");
		delayFrom.setDefaultValue("Amersham");

		HtmlSelect delayTo = form
				.getSelectByName("ctl00$cphMain$lb_stationofdelay2");
		delayTo.setDefaultValue("Watford");
}
		HtmlSelect journeyDate = form
				.getSelectByName("ctl00$cphMain$calJourneyDate$ddl_day");
		journeyDate.setDefaultValue("31");

		HtmlSelect journeyMonth = form
				.getSelectByName("ctl00$cphMain$calJourneyDate$ddl_month");
		journeyMonth.setDefaultValue("10");

		HtmlSelect journeyYear = form
				.getSelectByName("ctl00$cphMain$calJourneyDate$ddl_year");
		journeyYear.setDefaultValue("2013");

		HtmlSelect journeyStartHour = form
				.getSelectByName("ctl00$cphMain$lb_starttime_hour");
		journeyStartHour.setDefaultValue("10");

		HtmlSelect journeyStartMinute = form
				.getSelectByName("ctl00$cphMain$lb_starttime_minute");
		journeyStartMinute.setDefaultValue("10");

		HtmlSelect delayDate = form
				.getSelectByName("ctl00$cphMain$calDelayDate$ddl_day");
		delayDate.setDefaultValue("31");

		HtmlSelect delayMonth = form
				.getSelectByName("ctl00$cphMain$calDelayDate$ddl_month");
		delayMonth.setDefaultValue("10");

		HtmlSelect delayYear = form
				.getSelectByName("ctl00$cphMain$calDelayDate$ddl_year");
		delayYear.setDefaultValue("2013");

		HtmlSelect delayHour = form
				.getSelectByName("ctl00$cphMain$lb_delay_hour");
		delayHour.setDefaultValue("10");

		HtmlSelect delayMinute = form
				.getSelectByName("ctl00$cphMain$lb_delay_minute");
		delayMinute.setDefaultValue("10");

		HtmlSelect delayLengthHour = form
				.getSelectByName("ctl00$cphMain$lb_delay_length_hour");
		delayLengthHour.setDefaultValue("0");
		
		HtmlSelect delayLengthMinute = form
				.getSelectByName("ctl00$cphMain$lb_delay_length_minute");
		delayLengthMinute.setDefaultValue("40");
		
		HtmlCheckBoxInput confirmCheckBox = (HtmlCheckBoxInput) form
				.getFirstByXPath("//input[@id='cphMain_chk_confirmation']");
		confirmCheckBox.setChecked(true); 
		
		HtmlLabel legal = (HtmlLabel)form.getFirstByXPath("//label[@for='cphMain_chk_confirmation']");
		System.out.println("***" + legal.getTextContent() + "***");
		
		HtmlPage page = webClient.clickByName(form, "ctl00$cphMain$btn_submit");

		System.out.println(page.asText());
		
		webClient.closeAllWindows();

	}

}
