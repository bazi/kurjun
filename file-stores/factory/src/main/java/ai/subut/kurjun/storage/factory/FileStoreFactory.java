package ai.subut.kurjun.storage.factory;


import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

import ai.subut.kurjun.common.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;
import ai.subut.kurjun.storage.fs.FileSystemFileStoreFactory;
import ai.subut.kurjun.storage.s3.S3FileStoreFactory;


/**
 * File store factory for all available implementations of file stores.
 *
 */
public class FileStoreFactory
{

    /**
     * Property key for file store type.
     */
    public static final String TYPE = "file.storage.type";

    /**
     * File system backed file store type key.
     */
    public static final String FILE_SYSTEM = "fs";

    /**
     * Amazon S3 backed file store type key.
     */
    public static final String S3 = "s3";

    @Inject
    private KurjunProperties properties;

    @Inject
    private FileSystemFileStoreFactory fileSystemFileStoreFactory;

    @Inject
    private S3FileStoreFactory s3FileStoreFactory;


    /**
     * Creates file store for the supplied context. File store type is identified by
     * {@link FileStoreModule#FILE_STORE_TYPE} key.
     *
     * @param context
     * @return
     */
    public FileStore create( KurjunContext context )
    {
        Properties prop = properties.getContextProperties( context.getName() );
        String type = prop.getProperty( TYPE );

        if ( FILE_SYSTEM.equals( type ) )
        {
            return fileSystemFileStoreFactory.create( context );
        }

        if ( S3.equals( type ) )
        {
            return s3FileStoreFactory.create( context );
        }

        throw new ProvisionException( "Invalid context properties" );
    }

}
