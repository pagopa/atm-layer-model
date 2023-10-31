//package it.gov.pagopa.atmlayer.service.model.validators;
//
//import it.gov.pagopa.atmlayer.service.model.constraint.BankKeyConstraint;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//
//import java.util.List;
//
//public class BankKeyValidator implements ConstraintValidator<BankKeyConstraint, BankKeyDto> {
//
//    public static boolean isStringValid(String str) {
//        return !str.trim().isEmpty();
//    }
//
//    public static boolean isListValid(List<String> list) {
//        boolean check = false;
//        if (list == null) {
//            check = true;
//        } else if (!list.isEmpty()) {
//            for (String s: list) {
//                if (!isStringValid(s)){
//                    return false;
//                }
//            }
//            check = true;
//        }
//        return check;
//    }
//
//    @Override
//    public void initialize(BankKeyConstraint constraintAnnotation) {
//    }
//
//    @Override
//    public boolean isValid(BankKeyDto bankKeyDto, ConstraintValidatorContext constraintValidatorContext) throws RuntimeException{
//        boolean isValid = false;
//            if (bankKeyDto.getBranchId() == null) {
//                if (bankKeyDto.getTerminalId() == null) {
//                    isValid = true;
//                }
//            } else if (isStringValid(bankKeyDto.getBranchId())){
//                if (isListValid(bankKeyDto.getTerminalId())) {
//                    isValid = true;
//                } else {
//                    return isValid;
//                }
//            } else {
//                return isValid;
//            }
//        return isValid;
//    }
//}
