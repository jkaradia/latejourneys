package com.latejourneys.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.springframework.stereotype.Component;

@Component
public class TimeUtil {

	

	public int timeDifference(String startTime, String endTime) {
		String[] startTime2 = startTime.split(":");
		String[] endTime2 = endTime.split(":");

		int startHour = Integer.parseInt(startTime2[0]);
		int startMinute = Integer.parseInt(startTime2[1]);

		int endHour = Integer.parseInt(endTime2[0]);
		int endMinute = Integer.parseInt(endTime2[1]);

		return (endHour - startHour) * 60 + (endMinute - startMinute);

	}
	
	public String getOffsetDate(int numberOfDays) { 
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date currentDate = new Date();		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, numberOfDays);
		Date offsetDate = calendar.getTime();
		return df.format(offsetDate);
		
	}

}
