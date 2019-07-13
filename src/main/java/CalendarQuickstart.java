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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.GregorianCalendar;

public class CalendarQuickstart {
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
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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

    public static void main(String... args) throws IOException, GeneralSecurityException {

        if(args.length != 1) throw new RuntimeException("Missing task text as parameter");

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        // List the next 10 events from the primary calendar.
        DateTime start, end;
        Event event;
        int[] remind_days = { 1, 3, 8, 22, 120 };
        GregorianCalendar cal_now, cal_remind;

        cal_now = new java.util.GregorianCalendar();
        cal_now.set(java.util.Calendar.HOUR_OF_DAY, 19);
        cal_now.set(java.util.Calendar.MINUTE, 0);

        for (int remind_day : remind_days) {
            cal_remind = (GregorianCalendar) cal_now.clone();
            cal_remind.add(java.util.Calendar.DAY_OF_MONTH, remind_day);

            System.out.println(String.format("Doing - %d", remind_day));

            start = new DateTime(cal_remind.getTimeInMillis());
            end = new DateTime(cal_remind.getTimeInMillis() + 900000);

            event = new Event().setStart(new EventDateTime().setDateTime(start))
                    .setEnd(new EventDateTime().setDateTime(end)).setSummary(String.format("PLI: %s - rem %d", args[0], remind_day));
            
            service.events().insert("primary", event).execute();
            System.out.println(String.format("- done - %d", remind_day));
        }

        System.out.println("Done all");

        // DateTime st = event.getStart().getDateTime();
        // if (st == null) {
        // st = event.getStart().getDate();
        // }
        // System.out.printf("%s (%s)\n", event.getSummary(), st);
    }
}
