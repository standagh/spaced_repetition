package cz.hatua.cal;

public class Config {

	enum Action { ADD, LIST, DELETE };
	Action action;
	String topic;
	int[] days = { 1, 3, 8, 22, 120 }; 

	public String toString() {
		String s = "";
		for (int i = 0; i < days.length; i++) {
			if(i == 0) s = "[";
			if(i > 0) s = s + ",";
			s = s + " " + Integer.toString(days[i]);
		}
		s = s + " ]";
		return String.format("Action '%s' for topic '%s' and days '%s'", action.toString(), topic, s ); 
	}
	
}
