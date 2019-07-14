package cz.hatua.cal;

import java.util.logging.Logger;

public class CommandLineParser {
 
	static Logger log = Logger.getLogger(CommandLineParser.class.getName());
	
	public CommandLineParser( String[] args) {
		Logger log = Logger.getLogger(this.getClass().getName());
		log.info("This is log message");
		
	}
	
	/*
	 args:
	 0 ... command - add or list or delete
	 if add:
	 	1 ... topic
	 if delete:
	 	1 ... topic
	 if list
	 	1 ... topic or '*' means everything
	  
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
			if(!args[2].equals("days")) throw new RuntimeException("missing parameter days for action 'add'");
			if(args.length < 4) throw new RuntimeException("missing value for parameter 'days'");
			c.days = CommandLineParser.parseValueDays(args[3]);
		}
		
		log.info(String.format("Config: ", c));
		return c;
	}
	
	static int[] parseValueDays(String days) {
		String[] sd = days.split(",");
		int[] ret = new int[ sd.length ];
		
		for (int i = 0; i < sd.length; i++) {
			ret[i] = Integer.valueOf(sd[i].trim()); 
		}
		return ret;
	}
	
}
