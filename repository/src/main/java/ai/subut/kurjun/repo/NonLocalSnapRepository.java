package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunConstants;
import ai.subut.kurjun.metadata.common.snap.DefaultSnapMetadata;
import ai.subut.kurjun.model.index.ReleaseFile;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.NonLocalRepository;
import ai.subut.kurjun.model.security.Identity;
import ai.subut.kurjun.repo.cache.PackageCache;
import ai.subut.kurjun.repo.util.SecureRequestFactory;


/**
 * Non-local snap repository implementation.
 *
 */
class NonLocalSnapRepository extends RepositoryBase implements NonLocalRepository
{

    private static final Logger LOGGER = LoggerFactory.getLogger( NonLocalSnapRepository.class );

    static final String INFO_PATH = "info";
    static final String GET_PATH = "get";

    @Inject
    private Gson gson;
    private PackageCache cache;

    private final URL url;
    private final Identity identity;


    @Inject
    public NonLocalSnapRepository( PackageCache cache, @Assisted String url, @Assisted Identity identity )
    {
        this.cache = cache;
        this.identity = identity;
        try
        {
            this.url = new URL( url );
        }
        catch ( MalformedURLException ex )
        {
            throw new IllegalArgumentException( "Invalid url", ex );
        }
    }


    @Override
    public Identity getIdentity()
    {
        return identity;
    }


    @Override
    public URL getUrl()
    {
        return url;
    }


    @Override
    public boolean isKurjun()
    {
        return true;
    }


    @Override
    public Set<ReleaseFile> getDistributions()
    {
        throw new UnsupportedOperationException( "Not supported in snap repositories." );
    }


    @Override
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( INFO_PATH, makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                try
                {
                    String json = IOUtils.toString( ( InputStream ) resp.getEntity() );
                    return gson.fromJson( json, DefaultSnapMetadata.class );
                }
                catch ( IOException ex )
                {
                    LOGGER.error( "Failed to read response data", ex );
                }
            }
        }
        return null;
    }


    @Override
    public InputStream getPackageStream( Metadata metadata )
    {
        InputStream cachedStream = checkCache( metadata );
        if ( cachedStream != null )
        {
            return cachedStream;
        }

        SecureRequestFactory secreq = new SecureRequestFactory( this );
        WebClient webClient = secreq.makeClient( GET_PATH, makeParamsMap( metadata ) );
        if ( identity != null )
        {
            webClient.header( KurjunConstants.HTTP_HEADER_FINGERPRINT, identity.getKeyFingerprint() );
        }

        Response resp = webClient.get();
        if ( resp.getStatus() == Response.Status.OK.getStatusCode() )
        {
            if ( resp.getEntity() instanceof InputStream )
            {
                byte[] md5 = cacheStream( ( InputStream ) resp.getEntity() );
                return cache.get( md5 );
            }
        }
        return null;
    }


    private Map< String, String> makeParamsMap( Metadata metadata )
    {
        Map<String, String> params = new HashMap<>();
        if ( metadata.getMd5Sum() != null )
        {
            params.put( "md5", Hex.encodeHexString( metadata.getMd5Sum() ) );
        }
        if ( metadata.getName() != null )
        {
            params.put( "name", metadata.getName() );
        }
        if ( metadata.getVersion() != null )
        {
            params.put( "version", metadata.getVersion() );
        }
        return params;
    }


    private InputStream checkCache( Metadata metadata )
    {
        if ( metadata.getMd5Sum() != null )
        {
            if ( cache.contains( metadata.getMd5Sum() ) )
            {
                return cache.get( metadata.getMd5Sum() );
            }
        }
        else
        {
            SerializableMetadata m = getPackageInfo( metadata );
            if ( m != null && cache.contains( m.getMd5Sum() ) )
            {
                return cache.get( m.getMd5Sum() );
            }
        }
        return null;
    }


    private byte[] cacheStream( InputStream is )
    {
        Path target = null;
        try
        {
            target = Files.createTempFile( null, null );
            Files.copy( is, target, StandardCopyOption.REPLACE_EXISTING );
            return cache.put( target.toFile() );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to cache package", ex );
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
