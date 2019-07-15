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
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.GregorianCalendar;
import java.util.Iterator;

class EventCalGoogle implements EventCal {

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying these
     * scopes, delete your previously saved tokens/ folder.
     */
    // private static final List<String> SCOPES =
    // Collections.singletonList(CalendarScopes.CALENDAR);
    private static final List<String> SCOPES = Collections
            .singletonList("https://www.googleapis.com/auth/calendar.events");

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

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
		// TODO Auto-generated method stub
        Calendar service;
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
        ArrayList<SpacedEvent> spacedEvents = new ArrayList<SpacedEvent>(items.size());
        if (items.size() == 0) return spacedEvents;
        
        for (Iterator<Event> iterator = items.iterator(); iterator.hasNext();) {
			Event event = iterator.next();
			if(event.getSummary().startsWith("PLI: ")) {
				spacedEvents.add(new SpacedEvent(event.getSummary().substring(4)));
			}
		}

        return spacedEvents;
        
	}
	
	
}
