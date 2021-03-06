package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;

import com.google.inject.Inject;

import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.MetadataCache;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.repo.cache.MetadataCacheFactory;
import ai.subut.kurjun.repo.cache.PackageCache;


/**
 * Base abstract class for non-local repositories. Common operations to non-local repositories should go here.
 *
 */
abstract class NonLocalRepositoryBase extends RepositoryBase implements NonLocalRepository
{

    @Inject
    private PackageCache packageCache;

    @Inject
    private MetadataCacheFactory metadataCacheFactory;


    @Override
    public MetadataCache getMetadataCache()
    {
        return metadataCacheFactory.get( this );
    }


    protected abstract Logger getLogger();


    /**
     * Checks if there is a cached package file for the supplied meta data.
     *
     * @param metadata meta data for which package is looked up in cache
     * @return stream of a package file if found in cache; {@code null} otherwise
     */
    protected InputStream checkCache( Metadata metadata )
    {
        if ( metadata.getMd5Sum() != null )
        {
            if ( packageCache.contains( metadata.getMd5Sum() ) )
            {
                return packageCache.get( metadata.getMd5Sum() );
            }
        }
        else
        {
            SerializableMetadata m = getPackageInfo( metadata );
            if ( m != null && packageCache.contains( m.getMd5Sum() ) )
            {
                return packageCache.get( m.getMd5Sum() );
            }
        }
        return null;
    }


    /**
     * Caches the supplied input stream of a package file. MD5 checksum of the package is returned in response so that
     * stream can be retrieved from the cache by
     * {@link NonLocalRepositoryBase#checkCache(ai.subut.kurjun.model.metadata.Metadata)} method.
     *
     * @param is input stream of package file to cache
     * @return md5 checksum of the cached package file
     * @see NonLocalRepositoryBase#checkCache(ai.subut.kurjun.model.metadata.Metadata)
     */
    protected byte[] cacheStream( InputStream is )
    {
        Path target = null;
        try
        {
            target = Files.createTempFile( null, null );
            Files.copy( is, target, StandardCopyOption.REPLACE_EXISTING );
            return packageCache.put( target.toFile() );
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to cache package", ex );
        }
        finally
        {
            if ( target != null )
            {
                target.toFile().delete();
            }
        }
        return null;
    }


}

