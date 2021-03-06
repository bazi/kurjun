package ai.subut.kurjun.ar;


import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * The type of compression used.
 */
public enum  CompressionType
{
    NONE( null ), GZIP( "gz" ), BZIP2( "bz2" ), XZ ( "xz" ), LZMA ( "lzma" );


    // using this map is faster than standard Enum valueOf methods
    private static final Map<String,CompressionType> extensionMap;
    private final String extension;

    static {
        extensionMap = new HashMap<>( 5 );
        for ( CompressionType type: CompressionType.values() )
        {
            extensionMap.put( type.getExtension(), type );
        }
    }


    private CompressionType( String extension )
    {
        this.extension = extension;
    }


    public static String getExtension( String filename ) {
        if ( filename.lastIndexOf( '.' ) == -1 )
        {
            return null;
        }

        return filename.substring( filename.lastIndexOf( '.' ) + 1 );
    }


    public String getExtension()
    {
        return extension;
    }


    public static CompressionType getCompressionType( String filename )
    {
        CompressionType type = extensionMap.get( getExtension( filename ) );

        if ( type == null )
        {
            return NONE;
        }

        return type;
    }


    public static boolean isCompressed( File file )
    {
        return getCompressionType( file ) != NONE;
    }

    public static CompressionType getCompressionType( File file )
    {
        return getCompressionType( file.getName() );
    }


    public static CompressionType getByExtension( String ext )
    {
        if ( ext != null )
        {
            for ( CompressionType c : values() )
            {
                if ( ext.equalsIgnoreCase( c.getExtension() ) )
                {
                    return c;
                }
            }
        }
        return NONE;
    }


    /**
     * Makes string extension with dot prepended to be ready for use with standard file creation methods of JDK.
     *
     * @param compressionType compression type to make extension for
     * @return dot prepended extension if known compression type is supplied; otherwise {@code null} is returned, this
     * case includes {@link #NONE}.
     */
    public static String makeFileExtenstion( CompressionType compressionType )
    {
        if ( compressionType != null && compressionType != NONE )
        {
            return "." + compressionType.extension;
        }
        return null;
    }


}
