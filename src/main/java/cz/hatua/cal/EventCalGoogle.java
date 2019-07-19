package cz.hatua.cal;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

class EventCalGoogle implements EventCal {

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    static final Pattern SUMMARY_PATTERN = Pattern.compile("^(PLI: )(.*)( - rem) ([0-9]+)$");
    
    static final Logger logs = Logger.getLogger(EventCalGoogle.class.getName());
    
    /**
     * Global instance of the scopes required by this quickstart. If modifying these
     * scopes, delete your previously saved tokens/ folder.
     */
    // private static final List<String> SCOPES =
    // Collections.singletonList(CalendarScopes.CALENDAR);
    private static final List<String> SCOPES = Collections
            .singletonList("https://www.googleapis.com/auth/calendar.events");

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    Logger logd;
    EventCalGoogle() {
        logd = Logger.getLogger("mylog");
	}
    
    /**
     * Creates an authorized Credential object.
     * 
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = EventCalGoogle.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


//	EventCalGoogle() {
//		Logger log = Logger.getLogger(this.getClass().getName());
//	}

	@Override
	public void actionAdd(String topic, int[] remind_days) {
        DateTime start, end;
        Event event;
        GregorianCalendar cal_now, cal_remind;

        cal_now = new java.util.GregorianCalendar();
        cal_now.set(java.util.Calendar.HOUR_OF_DAY, 19);
        cal_now.set(java.util.Calendar.MINUTE, 0);

        Calendar service;
		try {
			service = this.getService();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        for (int remind_day : remind_days) {
            cal_remind = (GregorianCalendar) cal_now.clone();
            cal_remind.add(java.util.Calendar.DAY_OF_MONTH, remind_day);

            System.out.println(String.format("Doing - %d", remind_day));

            start = new DateTime(cal_remind.getTimeInMillis());
            end = new DateTime(cal_remind.getTimeInMillis() + 900000);

            event = new Event().setStart(new EventDateTime().setDateTime(start))
                    .setEnd(new EventDateTime().setDateTime(end)).setSummary(String.format("PLI: %s (r %d)", topic, remind_day));
            
            try {
				service.events().insert("primary", event).execute();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
            System.out.println(String.format("- done - %d", remind_day));
        }

        System.out.println("Done all");
    }
	
	
	Calendar getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();
        return service;
	}


	@Override
	public List<SpacedEvent> actionList(String topic, GregorianCalendar startDay) {
        Calendar service;
        this.logd.info("Doing list");
		try {
			service = this.getService();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		DateTime now = new DateTime(System.currentTimeMillis());
        Events events;
		try {
			events = service.events().list("primary")
			        .setMaxResults(100)
			        .setTimeMin(now)
			        .setOrderBy("startTime")
			        .setSingleEvents(true)
			        .execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
        List<Event> items = events.getItems();
        HashMap<String, SpacedEvent> spacedEvents = new HashMap<String, SpacedEvent>(items.size());
        if (items.size() == 0) return new ArrayList<SpacedEvent>(0);;
        
        for (Iterator<Event> iterator = items.iterator(); iterator.hasNext();) {
			Event event = iterator.next();
			
			try {
				Map<String, Object> summaryElements = EventCalGoogle.parseSummary(event.getSummary());
				
				String summ = (String)summaryElements.get("summ");
				Integer remd = (Integer)summaryElements.get("remd");
				
				SpacedEvent spacedEvnt = spacedEvents.get(summ);
				if( spacedEvnt == null ) {
					// We don't have this item
					logd.info(String.format("Creating new spaced event '%s'", summ));
					spacedEvnt = new SpacedEvent(summ);
					spacedEvents.put(summ, spacedEvnt);
				}
				
				spacedEvnt.addRemindDay((Integer)(summaryElements.get("remd")));

			} catch (ParseException e) {
				logd.severe(String.format("Parse exception for summary: '%s'",event.getSummary()));
				continue;
			}
		}

        return new ArrayList<SpacedEvent>(spacedEvents.values());
        
	}
	
	static Map<String, Object> parseSummary(String summary) throws ParseException {
		Map<String, Object> ret = new HashMap<String, Object>(2);

        Matcher m = EventCalGoogle.SUMMARY_PATTERN.matcher(summary);
        if( m.find() ) {
        	Integer remd =  Integer.valueOf(m.group(4));
        	logs.info(String.format("Parsing summary '%s' as: '%s'/'%d'", summary, m.group(2), remd));
        	ret.put("summ", m.group(2));
        	ret.put("remd", remd);
        	return ret;
        }
        
        throw new ParseException(String.format("Unable to parse summary '%s'", summary), -1);
	}
	
	
}
