package cz.hatua.cal;

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
		} else {
			log.info(String.format("Action '%s' not implemented", c.action));
		}
		
	}
	
}
