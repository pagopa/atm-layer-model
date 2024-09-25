package it.gov.pagopa.atmlayer.service.model.configurations;

import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

@ApplicationScoped
public class DirManager {

    @Getter
    public static File decodedFilesDirectory;

    @Startup
    static void init(@Observes StartupEvent ev) throws IOException {
        String secureDirPath = System.getProperty("java.io.tmpdir") + "/decodedFilesDirectory";
        decodedFilesDirectory = new File(secureDirPath);
        if (!decodedFilesDirectory.exists() && !decodedFilesDirectory.mkdirs()) {
            throw new IOException("Impossibile creare una directory sicura per il salvataggio di file temporanei.");
        }
        if (SystemUtils.IS_OS_UNIX) {
            Set<PosixFilePermission> dirPermissions = EnumSet.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
            );
            java.nio.file.Files.setPosixFilePermissions(decodedFilesDirectory.toPath(), dirPermissions);
        }
    }

    @Shutdown
    static void shutdown(@Observes ShutdownEvent ev) throws IOException {
        FileUtils.deleteDirectory(decodedFilesDirectory);
    }

    private DirManager() {
        throw new IllegalStateException("Utility class DirManager should not be instantiated");
    }
}