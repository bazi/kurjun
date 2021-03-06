package ai.subut.kurjun.storage.fs;


import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * File system backed file store factory implementation. Used for OSGi DI.
 *
 */
public class FileSystemFileStoreFactoryImpl implements FileSystemFileStoreFactory
{
    private KurjunProperties properties;


    public FileSystemFileStoreFactoryImpl( KurjunProperties properties )
    {
        this.properties = properties;
    }


    @Override
    public FileStore create( KurjunContext context )
    {
        return new FileSystemFileStore( properties, context );
    }

}

