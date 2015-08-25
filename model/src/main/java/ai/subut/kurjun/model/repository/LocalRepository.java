package ai.subut.kurjun.model.repository;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import ai.subut.kurjun.model.metadata.PackageMetadata;


/**
 * A non-virtual Repository that is locally hosted on this Kurjun server.
 */
public interface LocalRepository extends Repository
{

    /**
     * Initializes the local repository at the specified base directory.
     *
     * @param baseDirectory base directory of the local apt repo
     */
    void init( String baseDirectory );


    /**
     * Gets path to repositories base directory.
     *
     * @return path to base directory
     */
    Path getBaseDirectoryPath();


    PackageMetadata put( InputStream is ) throws IOException;
}
