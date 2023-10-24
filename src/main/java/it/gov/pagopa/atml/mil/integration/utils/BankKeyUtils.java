package it.gov.pagopa.atml.mil.integration.utils;

import it.gov.pagopa.atml.mil.integration.model.BankKey;

import java.util.List;

public class BankKeyUtils {

    public static boolean isBankKeyValid(BankKey bankKey) {
        boolean isValid=false;
        boolean validBranchId=isStringValid(bankKey.getBranchId());
        if(validBranchId){
            boolean validTerminalList=isListValid(bankKey.getTerminalId());
            if (validTerminalList){
                isValid=true;
            }
        }

        return isValid;
    }

    public static boolean isStringValid(String str){
        return !str.trim().equals("");
    }

    public static boolean isListValid(List<String> list){
        boolean check =false;
        if( list ==null){
            check=true;
        }else if( list.size()>0){
            check=true;
        }
        return check;
    }

}
