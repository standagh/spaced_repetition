package cz.hatua.cal;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/*
this is event
- topic
- days of repetition
 */

public class SpacedEvent {
	String topic;
	ArrayList<Integer> remindDays;
	GregorianCalendar firstDay;
	
	Logger log;
	
	SpacedEvent(String topic) {
		this.log = Logger.getLogger(this.getClass().getName());
		this.topic = topic;
		this.remindDays = new ArrayList<Integer>(8);
		this.firstDay = null;
	}
	
	void addRemindDay(Integer day) {
		log.info(String.format("Adding to '%s' - day '%d'", this.topic, day));
		this.remindDays.add(day);
	}

	/**
	 * 
	 * @param day remind day - whether it is 5th, 10th, 120th dat
	 * @param calDay - on which calendar day was remind day set, so it is possible to count original event day
	 */
	void setFirstDay(Integer day, GregorianCalendar calDay) {
		calDay.add(Calendar.DAY_OF_MONTH, -day);
		firstDay = calDay;
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(String.format("'%s' ", this.topic));
		if( firstDay != null ) {
		    ret.append(" (" + new SimpleDateFormat("yyyyMMdd").format(firstDay.getTime()) + ") ");
		}
		ret.append(" " + remindDays.toString() + " ");
		if( firstDay != null ) {
			ret.append(" " + getRemindDaysRelative(new GregorianCalendar()) + " ");
		}
		return ret.toString();
	}
	
	ArrayList<Integer> getRemindDaysRelative(GregorianCalendar gc) {
		if(firstDay == null) {
			throw new RuntimeException("Unable to calculate relative remind days. StartDay not set.");
		}

		SpacedEvent.normalizeEventTime(gc);
	        
		// count day diff
		long dayOffset = Duration.between(gc.toInstant(), firstDay.toInstant()).toDays();
		ArrayList<Integer> retAL = new ArrayList<Integer>(remindDays.size());
		for(Integer i: remindDays) {
			retAL.add(i + Integer.valueOf(Long.valueOf(dayOffset).intValue()));
		}
		return retAL;
	}
	
	static void normalizeEventTime(GregorianCalendar event) {
		event.set(java.util.Calendar.HOUR_OF_DAY, Config.EVENT_HOUR);
		event.set(java.util.Calendar.MINUTE, 0);
		event.set(java.util.Calendar.SECOND, 0);
		event.set(java.util.Calendar.MILLISECOND, 0);
	}
}
