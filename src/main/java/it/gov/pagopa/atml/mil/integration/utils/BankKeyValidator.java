package it.gov.pagopa.atml.mil.integration.utils;

import it.gov.pagopa.atml.mil.integration.constraint.BankKeyConstraint;
import it.gov.pagopa.atml.mil.integration.model.BankKey;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class BankKeyValidator implements ConstraintValidator<BankKeyConstraint, BankKey> {

    public static boolean isStringValid(String str) {
        return !str.trim().isEmpty();
    }

    public static boolean isListValid(List<String> list) {
        boolean check = false;
        if (list == null) {
            check = true;
        } else if (!list.isEmpty()) {
            for (String s: list) {
                if (!isStringValid(s)){
                    return false;
                }
            }
            check = true;
        }
        return check;
    }

    @Override
    public void initialize(BankKeyConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(BankKey bankKey, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        if (bankKey.getBranchId() == null) {
            if (bankKey.getTerminalId() == null) {
                isValid = true;
            }
        } else {
            isValid = isListValid(bankKey.getTerminalId());
        }
        return isValid;
    }
}
