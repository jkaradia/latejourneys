package com.latejourneys.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="prototype")
public class Journey implements Serializable {
	/**
	 * @return the fromStation
	 */
	public String getFromStation() {
		return fromStation;
	}

	/**
	 * @param fromStation
	 *            the fromStation to set
	 */
	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}

	/**
	 * @return the toStation
	 */
	public String getToStation() {
		return toStation;
	}

	/**
	 * @param toStation
	 *            the toStation to set
	 */
	public void setToStation(String toStation) {
		this.toStation = toStation;
	}

	public String toString() {
		return "Duration: " + duration + " From: " + fromStation + " To: "
				+ toStation + " StartTime: " + startTime + " EndTime: "
				+ endTime;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		calculateDuration();
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
		calculateDuration();
	}

	public long getDuration() {
		if (duration == null) {
			duration = TimeUnit.MICROSECONDS.toMinutes(endTime.getTime()
					- startTime.getTime());
		}
		return duration;
	}

	public void calculateDuration() {
		if (startTime != null && endTime != null && duration == null) {
			long difference = endTime.getTime()
					- startTime.getTime();
			duration = TimeUnit.MILLISECONDS.toMinutes(difference); 
		}
	}

	private String fromStation;
	private String toStation;
	private Date startTime;
	private Date endTime;
	private Long duration;
}
