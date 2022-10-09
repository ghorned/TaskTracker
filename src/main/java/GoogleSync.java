import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleSync {
    private static final String APPLICATION_NAME = "TaskTracker";
    private static final Credential AUTHORIZATION = getAuthorization();
    private static final Drive DRIVE_SERVICE = getDriveService();
    private static final Sheets SHEETS_SERVICE = getSheetsService();
    private static final String SPREADSHEET_ID = getSpreadsheetID();
    private static final String RANGE = "A1:F";

    // Starts up OAuth2.0 authentication and authorization flow
    private static Credential getAuthorization() {
        try {
            InputStream in = GoogleSync.class.getResourceAsStream("/credentials.json");
            if (in != null) {
                GoogleClientSecrets clientSecrets = GoogleClientSecrets
                        .load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));
                List<String> scopes = List.of(DriveScopes.DRIVE_FILE);
                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(), clientSecrets, scopes)
                        .setDataStoreFactory(new MemoryDataStoreFactory())
                        .setAccessType("offline")
                        .build();
                return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                        .authorize("user");
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Provides drive service to be used for making drive requests
    private static Drive getDriveService() {
        try {
            if (AUTHORIZATION != null) {
                return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(), AUTHORIZATION)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Provides sheets service to be used for making sheets requests
    private static Sheets getSheetsService() {
        try {
            if (AUTHORIZATION != null) {
                return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(), AUTHORIZATION)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Searches drive for 'TaskTracker' spreadsheet
    private static String getSpreadsheetID() {
        try {
            if (DRIVE_SERVICE != null) {
                FileList result = DRIVE_SERVICE.files().list()
                        .setQ("name = 'TaskTracker'")
                        .setSpaces("drive")
                        .execute();
                if (!result.getFiles().isEmpty()) {
                    return result.getFiles().get(0).getId();
                } else {
                    return createSpreadsheet();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Creates new spreadsheet if one does not exist
    private static String createSpreadsheet() {
        if (SHEETS_SERVICE != null) {
            try {
                Spreadsheet template = new Spreadsheet()
                        .setProperties(new SpreadsheetProperties()
                                .setTitle("TaskTracker"));
                Spreadsheet spreadsheet = SHEETS_SERVICE.spreadsheets()
                        .create(template)
                        .setFields("spreadsheetId")
                        .execute();
                return spreadsheet.getSpreadsheetId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Clears all data from spreadsheet before each save
    private static void clearSpreadsheet() {
        try {
            if (SHEETS_SERVICE != null && SPREADSHEET_ID != null) {
                ClearValuesRequest requestBody = new ClearValuesRequest();
                SHEETS_SERVICE.spreadsheets().values()
                        .clear(SPREADSHEET_ID, RANGE, requestBody)
                        .execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adds all updated data from to-do list to spreadsheet
    public static void updateSpreadsheet(List<List<Object>> data) {
        try {
            if (SHEETS_SERVICE != null && SPREADSHEET_ID != null) {
                clearSpreadsheet();
                ValueRange body = new ValueRange()
                        .setValues(data);
                SHEETS_SERVICE.spreadsheets().values()
                        .update(SPREADSHEET_ID, RANGE, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Forwards all data from spreadsheet to to-do list
    public static List<List<Object>> loadSpreadsheet() {
        try {
            if (SHEETS_SERVICE != null && SPREADSHEET_ID != null) {
                ValueRange response = SHEETS_SERVICE.spreadsheets().values().get(SPREADSHEET_ID, RANGE).execute();
                return response.getValues();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}