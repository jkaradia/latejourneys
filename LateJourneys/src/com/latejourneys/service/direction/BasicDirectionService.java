package com.latejourneys.service.direction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 

import com.latejourneys.proxy.htmlunit.BasicDirectionProxy;
import com.latejourneys.service.DirectionService;

@Service("directionService") 
public class BasicDirectionService implements DirectionService {

	@Autowired
	private BasicDirectionProxy directionProxy;
 

	/**
	 * @param directionProxy the directionProxy to set
	 */
	public void setDirectionProxy(BasicDirectionProxy directionProxy) {
		this.directionProxy = directionProxy;
	}


	 
	public int duration(String from, String to) {
		return directionProxy.duration(from, to);
	}

}