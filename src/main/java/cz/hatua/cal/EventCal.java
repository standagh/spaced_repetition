package cz.hatua.cal;

import java.util.GregorianCalendar;
import java.util.List;

interface EventCal {
	// defaults to from now
	void actionAdd(String topic, int[] days);
	void actionAdd(String topic, int[] days, GregorianCalendar startDay);
	// TODO tests and startDay
	List<SpacedEvent> actionList(String topic, GregorianCalendar startDay);
}
