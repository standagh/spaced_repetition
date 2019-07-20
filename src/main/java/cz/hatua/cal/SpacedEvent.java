package cz.hatua.cal;

import java.text.SimpleDateFormat;
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
		ret.append(String.format("'%s' -", this.topic));
		for (Integer rdval : remindDays) {
			ret.append(" " + rdval);
		}
		if( firstDay != null ) {
		    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		    fmt.setCalendar(firstDay);
		    String dateFormatted = fmt.format(firstDay.getTime());	
		    ret.append(" (" + dateFormatted + ") ");
			
		}
		return ret.toString();
	}
}
