package cz.hatua.cal;

import java.util.GregorianCalendar;
import java.util.List;

interface EventCal {
	void actionAdd(String topic, int[] days);
	// TODO tests and startDay
	List<SpacedEvent> actionList(String topic, GregorianCalendar startDay);
}
