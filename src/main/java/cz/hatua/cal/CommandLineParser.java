package cz.hatua.cal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class CommandLineParser {
 
	static Logger log = Logger.getLogger(CommandLineParser.class.getName());
	
	public CommandLineParser( String[] args) {
		Logger log = Logger.getLogger(this.getClass().getName());
		log.info("This is log message");
		
	}
	
	/*
	 params:
	 add <topic>
	 	days <list_of_days>
	 	startday <yyyyMMdd>
	 delete <topic>
	 list <topic>
	 
	  
	 */
	public static Config parseCMDParams(String... args) {
		Config c = new Config();
			
		if (args.length < 2) throw new RuntimeException("Invalid input parameters - at least 2 params expected. First is one of 'add|list|delete'");
		
		switch (args[0]) {
		case "add":
			c.action = Config.Action.ADD;
			break;
		case "list":
			c.action = Config.Action.LIST;
			break;
		case "delete":
			c.action = Config.Action.DELETE;
			break;

		default:
			throw new RuntimeException("Invalid param 1 - add / list / delete allowed");
		}
		
		c.topic = args[1];
		
		if(c.action == Config.Action.ADD && args.length > 2) {
			CommandLineParser.parseParamsActionAdd(args, c, 2);
		}
		
		log.info(String.format("Config: ", c));
		return c;
	}
	
	static void parseParamsActionAdd(String[] args, Config c, int argIndex) {
		while(true) {
			if(args.length <= argIndex) return;
			log.info( String.format("Parsing param: '%s'", args[argIndex]) );
			
			if(args[argIndex].equals("days")) {
				argIndex++;
				c.days = CommandLineParser.parseValueDays(args[argIndex++]);
				continue;
			}
			if(args[argIndex].equals("startday")) {
				argIndex++;
				c.startDay = CommandLineParser.parseValueStartDay(args[argIndex++]);
				continue;
			}
			throw new RuntimeException(String.format("Invalid parameter '%s'", args[argIndex]));
		}
	}
	
	static int[] parseValueDays(String days) {
		String[] sd = days.split(",");
		int[] ret = new int[ sd.length ];
		
		for (int i = 0; i < sd.length; i++) {
			ret[i] = Integer.valueOf(sd[i].trim()); 
		}
		return ret;
	}
	
	static GregorianCalendar parseValueStartDay(String start) {
		GregorianCalendar gc = new GregorianCalendar();
		try {
			gc.setTime(new SimpleDateFormat("yyyyMMdd").parse(start));
			return gc;
		} catch(ParseException e) {
			// this is same handling as IntegerValueOf - which is runtimeException successor
			throw new RuntimeException(e);
		}
	}
	
}
