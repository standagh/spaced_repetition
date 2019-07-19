package cz.hatua.cal;

import java.util.ArrayList;
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

	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(String.format("'%s' -", this.topic));
		for (Integer rdval : remindDays) {
			ret.append(" " + rdval);
		}
		return ret.toString();
	}
}
