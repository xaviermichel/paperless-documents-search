package fr.simple.edm.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import fr.simple.edm.GoogleSheetImporterConfiguration;
import fr.simple.edm.domain.AccountOperation;
import fr.simple.edm.service.exception.SheetNotExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class GoogleSheetTranslator {

    private final GoogleSheetImporterConfiguration googleSheetImporterConfiguration;
    private final GoogleSheetUtils googleSheetUtils;

    @Autowired
    public GoogleSheetTranslator(GoogleSheetImporterConfiguration googleSheetImporterConfiguration, GoogleSheetUtils googleSheetUtils) throws GeneralSecurityException, IOException {
        this.googleSheetImporterConfiguration = googleSheetImporterConfiguration;
        this.googleSheetUtils = googleSheetUtils;
    }

    private Object emptyIfNull(Object o) {
        if (StringUtils.isEmpty(o)) {
            return "";
        }
        return o;
    }

    public void clear() throws IOException {
        log.info("Start of clear");
        googleSheetUtils.clearRange(googleSheetImporterConfiguration.getSpreadsheetId(), googleSheetImporterConfiguration.getSmartInsertSheet().getRange());
        googleSheetUtils.clearRange(googleSheetImporterConfiguration.getSpreadsheetId(), googleSheetImporterConfiguration.getNextMonthSheet().getRange());
        log.info("End of clear");
    }


    public void reloadData(List<AccountOperation> operations) throws IOException, GeneralSecurityException, SheetNotExistsException {
        log.info("Start of reloadData");

        // construct request
        List<List<Object>> values = new ArrayList<>();
        for (AccountOperation operation: operations) {
            List<Object> operationValues = Arrays.asList(
                emptyIfNull(operation.getDate()),
                emptyIfNull(operation.getLabel()),
                emptyIfNull(operation.getDebitValue()),
                emptyIfNull(operation.getCreditValue()),
                emptyIfNull(operation.getAccountLabel())
            );
            values.add(operationValues);
        }

        googleSheetUtils.writeValues(googleSheetImporterConfiguration.getSpreadsheetId(), googleSheetImporterConfiguration.getSmartInsertSheet().getRange(), values);

        googleSheetUtils.executeCopyPasteRequest(
            googleSheetImporterConfiguration.getSpreadsheetId(),
            googleSheetImporterConfiguration.getSmartInsertSheetFormulaSource(),
            googleSheetImporterConfiguration.getSmartInsertSheetFormulaDestination(),
            "PASTE_FORMULA"
        );

        googleSheetUtils.executeCopyPasteRequest(
            googleSheetImporterConfiguration.getSpreadsheetId(),
            googleSheetImporterConfiguration.getSmartInsertSheetFullRangeCopy(),
            googleSheetImporterConfiguration.getNextMonthSheetPaste(),
            "PASTE_VALUES"
        );

        log.info("End of reloadData");
    }

}
