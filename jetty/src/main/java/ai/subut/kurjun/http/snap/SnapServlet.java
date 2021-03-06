package ai.subut.kurjun.http.snap;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.utils.SnapUtils;
import ai.subut.kurjun.http.HttpServer;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.http.ServletUtils;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.security.Permission;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.service.AuthManager;


@Singleton
class SnapServlet extends HttpServletBase
{

    private static final Logger LOGGER = LoggerFactory.getLogger( SnapServlet.class );

    static final String SNAPS_GET_PATH = "get";
    static final String SNAPS_INFO_PATH = "info";

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private AuthManager authManager;

    private KurjunContext context;


    @Override
    public void init() throws ServletException
    {
        this.context = HttpServer.CONTEXT;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !checkAuthentication( req, Permission.GET_PACKAGE ) )
        {
            forbidden( resp );
            return;
        }

        List<String> paths = ServletUtils.splitPath( req.getPathInfo() );
        if ( paths.size() == 1 )
        {
            String pathItem = paths.get( 0 );

            DefaultMetadata meta = new DefaultMetadata();
            meta.setMd5sum( getMd5ParameterValue( req ) );
            meta.setName( req.getParameter( NAME_PARAM ) );
            meta.setVersion( req.getParameter( VERSION_PARAM ) );

            if ( meta.getMd5Sum() == null && meta.getName() == null )
            {
                String msg = "Neither 'md5' nor 'name' and 'version' params specified";
                badRequest( resp, msg );
                return;
            }

            if ( pathItem.equals( SNAPS_GET_PATH ) )
            {
                streamPackage( meta, resp );
            }
            else if ( pathItem.equals( SNAPS_INFO_PATH ) )
            {
                respondPackageInfo( meta, resp );
            }
            else
            {
                badRequest( resp, "Invalid request path: " + req.getPathInfo() );
            }
        }
        else
        {
            badRequest( resp, "Invalid request path: " + req.getPathInfo() );
        }
    }


    @Override
    protected void doDelete( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        if ( !checkAuthentication( req, Permission.DEL_PACKAGE ) )
        {
            forbidden( resp );
            return;
        }

        byte[] md5 = getMd5ParameterValue( req );
        if ( md5 != null )
        {
            LocalRepository repo = getRepository();
            boolean deleted = repo.delete( md5 );
            if ( deleted )
            {
                ok( resp, "Package successfully removed" );
            }
            else
            {
                String msg = "Package not deleted";
                writeResponse( resp, HttpServletResponse.SC_ACCEPTED, msg );
            }
        }
        else
        {
            badRequest( resp, "Provide md5 checksum of the package to remove" );
        }
    }


    @Override
    protected KurjunContext getContext()
    {
        return context;
    }


    @Override
    protected AuthManager getAuthManager()
    {
        return authManager;
    }


    private void streamPackage( Metadata meta, HttpServletResponse resp ) throws IOException
    {
        LocalRepository repo = getRepository();
        SerializableMetadata m = repo.getPackageInfo( meta );
        if ( m != null )
        {
            try ( InputStream is = repo.getPackageStream( m ) )
            {
                resp.setStatus( HttpServletResponse.SC_OK );
                resp.setHeader( "Content-Disposition", "attachment; filename=" + SnapUtils.makeFileName( m ) );
                IOUtils.copy( is, resp.getOutputStream() );
            }
        }
        else
        {
            notFound( resp, "Package not found." );
        }
    }


    private void respondPackageInfo( Metadata meta, HttpServletResponse resp ) throws IOException
    {
        LocalRepository repo = getRepository();
        SerializableMetadata m = repo.getPackageInfo( meta );
        if ( m != null )
        {
            resp.setContentType( "application/json" );
            try ( PrintWriter writer = resp.getWriter() )
            {
                writer.print( m.serialize() );
            }
        }
        else
        {
            notFound( resp, "Package not found." );
        }
    }


    private LocalRepository getRepository()
    {
        return repositoryFactory.createLocalSnap( context );
    }


}

