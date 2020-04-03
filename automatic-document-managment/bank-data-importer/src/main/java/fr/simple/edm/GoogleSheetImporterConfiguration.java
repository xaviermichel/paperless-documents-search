package fr.simple.edm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.sheet")
@Data
public class GoogleSheetImporterConfiguration {

    private String appName;
    private String credentialsFilePath;
    private String tokenDirectoryPath;
    private String spreadsheetId;
    private String oauthCallbackIP = "127.0.0.1";
    private Sheet smartInsertSheet;
    private Sheet nextMonthSheet;
    private Sheet smartInsertSheetFormulaSource;
    private Sheet smartInsertSheetFormulaDestination;
    private Sheet smartInsertSheetFullRangeCopy;
    private Sheet nextMonthSheetPaste;

    @Data
    public static class Sheet {
        private String name;
        private String firstRow;
        private String lastRow;
        private String firstCol;
        private String lastCol;

        public String getRange() {
            return  name + "!" + firstCol + firstRow + ":" + lastCol + lastRow;
        }
    }
}
