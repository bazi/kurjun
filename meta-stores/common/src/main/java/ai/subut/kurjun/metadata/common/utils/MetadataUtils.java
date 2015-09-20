package ai.subut.kurjun.metadata.common.utils;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.subut.kurjun.metadata.common.apt.DefaultDependency;
import ai.subut.kurjun.metadata.common.apt.DefaultIndexPackageMetaData;
import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.index.IndexPackageMetaData;
import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.PackageMetadata;


public class MetadataUtils
{

    public static final Gson JSON;


    static
    {
        GsonBuilder gb = new GsonBuilder();

        // register Dependency type adapter for correct deserialization of fields like List<Dependency>
        gb.registerTypeAdapter( Dependency.class, new DependencyTypeAdapter() );

        JSON = gb.create();
    }


    private MetadataUtils()
    {
        // utility class
    }


    /**
     * Converts supplied package meta data instance to a serializable form. Package metadata implementations may not be
     * serializable. This method ensures that the returned instance is serializable.
     *
     * @param meta meta data to convert
     * @return serializable instance of the supplied meta data
     */
    public static DefaultPackageMetadata serializablePackageMetadata( PackageMetadata meta )
    {
        if ( meta instanceof DefaultPackageMetadata )
        {
            return ( DefaultPackageMetadata ) meta;
        }

        DefaultPackageMetadata result = new DefaultPackageMetadata();
        copyPackageMetadata( meta, result );
        return result;
    }


    /**
     * Converts supplied package meta data instance to a serializable form. Package metadata implementations may not be
     * serializable. This method ensures that the returned instance is serializable.
     *
     * @param meta meta data to convert
     * @return serializable instance of the supplied meta data
     */
    public static DefaultIndexPackageMetaData serializableIndexPackageMetadata( IndexPackageMetaData meta )
    {
        if ( meta instanceof DefaultIndexPackageMetaData )
        {
            return ( DefaultIndexPackageMetaData ) meta;
        }

        DefaultIndexPackageMetaData result = new DefaultIndexPackageMetaData();
        copyPackageMetadata( meta, result );

        result.setSha1( meta.getSHA1() );
        result.setSha256( meta.getSHA256() );
        result.setSize( meta.getSize() );
        result.setDescriptionMd5( meta.getDescriptionMd5() );
        result.setTag( meta.getTag() );

        return result;
    }


    private static void copyPackageMetadata( PackageMetadata source, DefaultPackageMetadata target )
    {
        target.setMd5( source.getMd5Sum() );
        target.setComponent( source.getComponent() );
        target.setFilename( source.getFilename() );
        target.setPackage( source.getPackage() );
        target.setVersion( source.getVersion() );
        target.setMaintainer( source.getMaintainer() );
        target.setArchitecture( source.getArchitecture() );
        target.setInstalledSize( source.getInstalledSize() );
        target.setDependencies( cloneDependencies( source.getDependencies() ) );
        target.setRecommends( cloneDependencies( source.getRecommends() ) );
        target.setSuggests( cloneDependencies( source.getSuggests() ) );
        target.setEnhances( cloneDependencies( source.getEnhances() ) );
        target.setPreDepends( cloneDependencies( source.getPreDepends() ) );
        target.setConflicts( cloneDependencies( source.getConflicts() ) );
        target.setBreaks( cloneDependencies( source.getBreaks() ) );
        target.setReplaces( cloneDependencies( source.getReplaces() ) );
        target.setProvides( source.getProvides() != null ? new ArrayList<>( source.getProvides() ) : null );
        target.setSection( source.getSection() );
        target.setPriority( source.getPriority() );
        target.setHomepage( source.getHomepage() );
        target.setDescription( source.getDescription() );
    }


    private static List<Dependency> cloneDependencies( List<Dependency> dependencies )
    {
        if ( dependencies == null )
        {
            return null;
        }

        List<Dependency> result = new ArrayList<>();
        for ( Dependency dependency : dependencies )
        {
            DefaultDependency dep = new DefaultDependency();
            dep.setPackage( dependency.getPackage() );
            dep.setVersion( dependency.getVersion() );
            dep.setDependencyOperator( dependency.getDependencyOperator() );
            dep.setAlternatives( cloneDependencies( dependency.getAlternatives() ) );
            result.add( dep );
        }
        return result;
    }
}

