package ai.subut.kurjun.snap.metadata.store;


import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import ai.subut.kurjun.db.file.FileDbModule;
import ai.subut.kurjun.model.metadata.snap.SnapMetadataStore;


/**
 * Guice module to initialize snap package store bindings.
 *
 */
public class SnapMetadataStoreModule extends AbstractModule
{

    private String fileDbPath;


    /**
     * Snap metadata store needs a file path where metadata is stored.
     *
     * @param fileDbPath file path to store metadata
     */
    public SnapMetadataStoreModule( String fileDbPath )
    {
        this.fileDbPath = fileDbPath;
    }


    @Override
    protected void configure()
    {
        bind( String.class ).annotatedWith( Names.named( FileDbModule.DB_FILE_PATH ) ).toInstance( fileDbPath );

        bind( SnapMetadataStore.class ).to( SnapMetadataStoreImpl.class );
    }

}

