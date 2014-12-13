package ai.subut.kurjun.ar;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Interface for Ar (archive) commands.
 */
public interface Ar {
    public List<ArArchiveEntry> list() throws IOException;
    public void extract( File extractTo, ArArchiveEntry entry ) throws IOException;
}
