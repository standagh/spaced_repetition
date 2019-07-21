package cz.hatua.cal;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.GregorianCalendar;

public class Config {

	enum Action { ADD, LIST, DELETE };
	Action action;
	String topic;
	int[] days = { 1, 3, 8, 22, 120 };
	GregorianCalendar startDay = null;

	public String toString() {
		String startDayString = "now";
		if(startDay != null) {
			startDayString = new SimpleDateFormat("yyyy-MM-dd").format(startDay.getTime());
		}
		return String.format("Action '%s' for topic '%s' and days '%s' starting from '%s'", action.toString(), topic, Arrays.toString(days), startDayString ); 
	}
	
}
