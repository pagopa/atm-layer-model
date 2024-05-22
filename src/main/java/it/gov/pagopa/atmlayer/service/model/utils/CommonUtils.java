package it.gov.pagopa.atmlayer.service.model.utils;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonUtils {

    private CommonUtils(){
        throw new IllegalStateException("Utility class");
    }

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

    public static String getRelativePath(String basePath, String absolutePath) {
        Path base = Paths.get(basePath);
        Path absolute = Paths.get(absolutePath);
        Path relativePath = base.relativize(absolute);
        return relativePath.toString();
    }
}
