package cz.hatua.cal;

import java.util.GregorianCalendar;
import java.util.List;

interface EventCal {
	void actionAdd(String topic, int[] days, GregorianCalendar startDay);

	List<SpacedEvent> actionList(String topic, GregorianCalendar startDay, GregorianCalendar endDay);

	/**
	 * returns number of events deleted
	 */
	int actionDelete(String topic, GregorianCalendar startDay, GregorianCalendar endDay);
}
