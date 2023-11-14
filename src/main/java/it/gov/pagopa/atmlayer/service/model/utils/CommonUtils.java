package it.gov.pagopa.atmlayer.service.model.utils;

import org.apache.commons.io.FilenameUtils;

public class CommonUtils {

    public static String getPathWithoutFilename(String path) {
        return path.substring(0, path.length() - 1);
    }

    public static String getFilenameWithExtension(String fileName, String extension){
        return fileName.concat(".").concat(extension);
    }

    public static String getFilenameWithExtensionFromStorageKey(String storageKey){
        String fileName = FilenameUtils.getBaseName(storageKey);
        String extension = FilenameUtils.getExtension(storageKey);
        return getFilenameWithExtension(fileName,extension);
    }
}
