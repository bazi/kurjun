package ai.subut.kurjun.http.apt;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.security.service.AuthManager;


@Singleton
class LocalAptRepoServlet extends HttpServletBase
{

    @Inject
    private AuthManager authManager;

    private LocalRepository repository;
    private Path baseDirectory;


    @Inject
    public LocalAptRepoServlet( RepositoryFactory repositoryFactory )
    {
        // TODO: parent dir from config
        this.baseDirectory = Paths.get( "/var/www/repos/apt/hub" );
        repository = repositoryFactory.createLocalAptWrapper( baseDirectory.toString() );
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        resp.setContentType( "text/plain" );
        resp.setCharacterEncoding( StandardCharsets.UTF_8.name() );

        String pathWithoutLeadingSlash = req.getPathInfo().substring( 1 );

        Path path = baseDirectory.resolve( pathWithoutLeadingSlash );
        if ( Files.exists( path ) )
        {
            try ( ServletOutputStream out = resp.getOutputStream() )
            {
                Files.copy( path, out );
            }
        }
        else
        {
            notFound( resp, "Specified path does not exist or is not a file" );
        }
    }


    @Override
    protected KurjunContext getContext()
    {
        throw new UnsupportedOperationException( "No context for apt repo wrapper." );
    }


    @Override
    protected AuthManager getAuthManager()
    {
        return authManager;
    }


}

