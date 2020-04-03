package fr.simple.edm.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import fr.simple.edm.GoogleSheetImporterConfiguration;
import fr.simple.edm.service.exception.SheetNotExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GoogleSheetUtils {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final GoogleSheetImporterConfiguration googleSheetImporterConfiguration;

    private Sheets service;

    @Autowired
    public GoogleSheetUtils(GoogleSheetImporterConfiguration googleSheetImporterConfiguration) throws GeneralSecurityException, IOException {
        this.googleSheetImporterConfiguration = googleSheetImporterConfiguration;
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(googleSheetImporterConfiguration.getAppName())
            .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream(new File(googleSheetImporterConfiguration.getCredentialsFilePath()));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(googleSheetImporterConfiguration.getTokenDirectoryPath())))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(googleSheetImporterConfiguration.getOauthCallbackIP()).setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void clearRange(String spreadsheetId, String range) throws IOException {
        ClearValuesRequest requestBody = new ClearValuesRequest();
        Sheets.Spreadsheets.Values.Clear request = service.spreadsheets().values().clear(spreadsheetId, range, requestBody);
        ClearValuesResponse response = request.execute();

        log.info("Sheet have been cleared : {}", response);
    }

    public Integer getSheetIdBySheetName(String spreadsheetId, String sheetName) throws SheetNotExistsException, IOException {
        Optional<Integer> sheetId = service.spreadsheets().get(spreadsheetId).execute().getSheets().stream()
            .filter(s -> s.getProperties().getTitle().equalsIgnoreCase(sheetName))
            .map(s -> s.getProperties().getSheetId())
            .findFirst();

        log.info("Found smartInsertSheetId for {} = {}", sheetName, sheetId);
        if (! sheetId.isPresent()) {
            throw new SheetNotExistsException();
        }
        return sheetId.get();
    }

    public void writeValues(String spreadsheetId, String range, List<List<Object>> values) throws IOException {
        ValueRange requestBody = new ValueRange();
        requestBody.setValues(values);

        Sheets.Spreadsheets.Values.Update request = service.spreadsheets().values().update(spreadsheetId, range, requestBody);
        request.setValueInputOption("USER_ENTERED");
        UpdateValuesResponse response = request.execute();
        log.info("Data have been wrote : {}", response);
    }

    public void executeCopyPasteRequest(String spreadsheetId, GoogleSheetImporterConfiguration.Sheet source, GoogleSheetImporterConfiguration.Sheet destination, String pasteType) throws IOException, SheetNotExistsException {
        CopyPasteRequest extendFormula = new CopyPasteRequest();
        extendFormula.setSource(
            new GridRange()
                .setSheetId(getSheetIdBySheetName(spreadsheetId, source.getName()))
                .setStartRowIndex(Integer.valueOf(source.getFirstRow()))
                .setEndRowIndex(Integer.valueOf(source.getLastRow()) + 1)
                .setStartColumnIndex(Integer.valueOf(source.getFirstCol()))
                .setEndColumnIndex(Integer.valueOf(source.getLastCol()) + 1)
        );
        extendFormula.setDestination(
            new GridRange()
                .setSheetId(getSheetIdBySheetName(spreadsheetId, destination.getName()))
                .setStartRowIndex(Integer.valueOf(destination.getFirstRow()))
                .setEndRowIndex(Integer.valueOf(destination.getLastRow()) + 1)
                .setStartColumnIndex(Integer.valueOf(destination.getFirstCol()))
                .setEndColumnIndex(Integer.valueOf(destination.getLastCol()) + 1)
        );
        extendFormula.setPasteType(pasteType);

        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setCopyPaste(extendFormula));

        BatchUpdateSpreadsheetRequest updatingRequestBody = new BatchUpdateSpreadsheetRequest();
        updatingRequestBody.setRequests(requests);

        Sheets.Spreadsheets.BatchUpdate updatingRequest = service.spreadsheets().batchUpdate(spreadsheetId, updatingRequestBody);
        BatchUpdateSpreadsheetResponse updatingResponse = updatingRequest.execute();

        log.info("Data have been updated : {}", updatingResponse);
    }
}
