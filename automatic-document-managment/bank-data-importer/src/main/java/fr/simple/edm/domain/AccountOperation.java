package fr.simple.edm.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountOperation {

    private String accountLabel;

    private String date;

    private String label = "";

    private Double debitValue;

    private Double creditValue;

    // add some String in label, after new line
    public void addToLabel(String s) {
        s = sanitizeLabel(s);
        if (s.isEmpty()) {
            return;
        }
        if (! label.isEmpty()) {
            label += "\n";
        }
        label += s;
    }

    private String sanitizeLabel(String str) {
        str = removeFirstCharIfDoubleQuote(str);
        str = removeLastCharIfDoubleQuote(str);
        return str.trim();
    }

    private String removeFirstCharIfDoubleQuote(String str) {
        if (str.startsWith("\"")) {
            return str.substring(1);
        }
        return str;
    }
    private String removeLastCharIfDoubleQuote(String str) {
        if (str.endsWith("\"")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
