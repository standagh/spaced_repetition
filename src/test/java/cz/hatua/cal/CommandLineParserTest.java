package cz.hatua.cal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommandLineParserTest {

	@Test
	public void t1() {
		Config c = CommandLineParser.parseCMDParams(new String[] {"add", "topic"});
		String s = c.toString();
		assertTrue(s, s.equals("Action 'ADD' for topic 'topic' and days '[ 1, 3, 8, 22, 120 ]'"));
	}

	@Test
	public void t2() {
		Config c = CommandLineParser.parseCMDParams(new String[] {"add", "topic", "days", "1,2,3"});
		String s = c.toString();
		assertTrue(s, s.equals("Action 'ADD' for topic 'topic' and days '[ 1, 2, 3 ]'"));
	}

	
}
