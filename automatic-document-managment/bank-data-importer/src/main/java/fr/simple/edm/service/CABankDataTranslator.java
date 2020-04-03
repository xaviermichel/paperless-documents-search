package fr.simple.edm.service;

import fr.simple.edm.CAConfiguration;
import fr.simple.edm.domain.AccountOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CABankDataTranslator implements BankDataTranslator {

    private CAConfiguration caConfiguration;

    Pattern newTransactionLinePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4};");

    @Autowired
    public CABankDataTranslator(CAConfiguration caConfiguration) {
        this.caConfiguration = caConfiguration;
    }

    @Override
    public List<AccountOperation> fileToAccountOperations(String file) throws IOException {

        List<AccountOperation> operations = new ArrayList<>();

        String line = "";
        int currentAccountIndex = -1;
        BufferedReader br = new BufferedReader(new FileReader(file));

        AccountOperation currentOperation = null;
        while ((line = br.readLine()) != null) {

            log.debug("read line : {}", line);

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("Liste")) {
                currentAccountIndex++;
            }

            Matcher newTransaction = newTransactionLinePattern.matcher(line);
            boolean isNewTransaction = newTransaction.find();
            boolean isEndOfTransaction = line.endsWith(";");

            if (isNewTransaction) {
                log.debug("new transaction line : {}", line);
                currentOperation = new AccountOperation();

                currentOperation.setDate(line.split(";")[0]);
                currentOperation.addToLabel(line.split(";")[1]);
            }

            // some more ONLY label line
            if (currentOperation != null && ! isNewTransaction && ! isEndOfTransaction) {
                currentOperation.addToLabel(line);
            }

            // end of transaction
            if (currentOperation != null && isEndOfTransaction) {
                String[] splittedLine = line.split(";");

                // if there is a last part of label, include it
                if (! isNewTransaction) {
                    currentOperation.addToLabel(splittedLine[0]);
                }

                ArrayUtils.reverse(splittedLine);
                boolean isCredit = ! line.endsWith(";;");
                if (isCredit) {
                    currentOperation.setCreditValue(Double.valueOf(splittedLine[0].replaceAll("[^0-9,]+", "").replace(",", ".")));
                }
                else {
                    currentOperation.setDebitValue(Double.valueOf(splittedLine[0].replaceAll("[^0-9,]+", "").replace(",", ".")));
                }

                currentOperation.setAccountLabel(caConfiguration.getAccountsLabel().get(currentAccountIndex));

                operations.add(currentOperation);
                currentOperation = null;
            }
        }

        return operations;
    }

}
