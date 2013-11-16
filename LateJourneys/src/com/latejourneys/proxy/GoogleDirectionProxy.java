package com.latejourneys.proxy;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
 
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleDirectionProxy {

	private static Map<SimpleEntry<String, String>, Integer> journeyMap = new HashMap<SimpleEntry<String,String>, Integer>();
	
	static {
		journeyMap.put(new SimpleEntry<String, String>("Alperton","Rayners Lane"), 25);
		journeyMap.put(new SimpleEntry<String, String>("Acton Town","Rayners Lane"), 14);
		journeyMap.put(new SimpleEntry<String, String>("Barbican","Rayners Lane"), 46); 
	}
		
	
	public int duration(String from, String to) { 
		 SimpleEntry<String, String> entry = new SimpleEntry<String, String>(from,to);
		 Integer duration = journeyMap.get(entry);
		 if (duration == null) {
			 entry = new SimpleEntry<String, String>(to,from);
		 }
		 
		 if (duration == null) {
			 duration = 0;
		 }
		 
		 return duration;
	}
		 
		     

}