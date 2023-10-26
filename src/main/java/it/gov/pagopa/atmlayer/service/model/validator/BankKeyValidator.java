package it.gov.pagopa.atmlayer.service.model.validator;

import it.gov.pagopa.atmlayer.service.model.constraint.BankKeyConstraint;
import it.gov.pagopa.atmlayer.service.model.dto.BankKeyDto;
import it.gov.pagopa.atmlayer.service.model.dto.BranchDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class BankKeyValidator implements ConstraintValidator<BankKeyConstraint, BankKeyDto> {

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
    public boolean isValid(BankKeyDto bankKeyDto, ConstraintValidatorContext constraintValidatorContext) throws RuntimeException{
        boolean isValid = false;
        List<BranchDto> branches = bankKeyDto.getBranches();
        for (BranchDto branch: branches) {
            if (branch.getBranchId() == null) {
                if (branch.getTerminalId() == null) {
                    isValid = true;
                }
            } else if (isStringValid(branch.getBranchId())){
                if (isListValid(branch.getTerminalId())) {
                    isValid = true;
                } else {
                    isValid = false;
                    return isValid;
                }
            } else {
                isValid = false;
                return isValid;
            }
        }
        return isValid;
    }
}
