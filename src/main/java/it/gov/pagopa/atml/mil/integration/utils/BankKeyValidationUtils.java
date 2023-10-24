package it.gov.pagopa.atml.mil.integration.utils;

import it.gov.pagopa.atml.mil.integration.model.BankKey;

public class BankKeyValidationUtils {

    public static boolean isBankKeyValid(BankKey bankKey) {
        if (bankKey.getBranchId() == null && bankKey.getTerminalId() == null)
        if (bankKey.getBranchId() == null && bankKey.getTerminalId() != null) {
            return false;
        } else if (bankKey.getTerminalId().isEmpty() || bankKey.getBranchId().isEmpty()) {
            return false;
        }
    }
}
