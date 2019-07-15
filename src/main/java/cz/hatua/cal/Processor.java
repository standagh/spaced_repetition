package cz.hatua.cal;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Processor {

	static Logger log = Logger.getLogger(Processor.class.getName());

	public static void main(String... args) {
		Processor.process(args);
	}
	
	static void process(String... args) {
		Config c = CommandLineParser.parseCMDParams(args);
		EventCal ec = new EventCalGoogle();
		
		if(c.action == Config.Action.ADD) {
			ec.actionAdd(c.topic, c.days);
		} else if(c.action == Config.Action.LIST) {
			List<SpacedEvent> se = ec.actionList("*", null);
			for (Iterator<SpacedEvent> iterator = se.iterator(); iterator.hasNext();) {
				SpacedEvent spacedEvent = (SpacedEvent) iterator.next();
				System.out.print("Spaced event: " + spacedEvent.topic + "\n");
			}
		} else {
			log.info(String.format("Action '%s' not implemented", c.action));
		}
		
	}
	
}
