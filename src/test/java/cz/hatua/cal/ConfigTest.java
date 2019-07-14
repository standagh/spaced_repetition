package cz.hatua.cal;

import org.junit.Test;

import cz.hatua.cal.Config.Action;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

public class ConfigTest {

	Logger log;
	
	public ConfigTest() {
		this.log = Logger.getLogger(this.getClass().getName());
	}
	
	@Test
	public void init1() {
		Config c = new Config();
		c.action = Action.ADD;
		c.topic = "New entry";
		String s = c.toString();
		assertTrue(s, s.equals("Action 'ADD' for topic 'New entry' and days '[ 1, 3, 8, 22, 120 ]'"));
	}
	
	
}
