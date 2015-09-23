package ai.subut.kurjun.metadata.common.subutai;


import java.util.Arrays;

import ai.subut.kurjun.metadata.common.utils.MetadataUtils;
import ai.subut.kurjun.model.metadata.Architecture;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.SubutaiTemplateMetadata;


/**
 * Default serializable POJO implementation class of {@link SubutaiTemplateMetadata}.
 *
 */
public class DefaultTemplate implements SubutaiTemplateMetadata, SerializableMetadata
{

    private byte[] md5Sum;
    private String name;
    private String version;
    private Architecture architecture;


    @Override
    public byte[] getMd5Sum()
    {
        return md5Sum != null ? Arrays.copyOf( md5Sum, md5Sum.length ) : null;
    }


    public void setMd5Sum( byte[] md5Sum )
    {
        this.md5Sum = Arrays.copyOf( md5Sum, md5Sum.length );
    }


    @Override
    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public Architecture getArchitecture()
    {
        return architecture;
    }


    public void setArchitecture( Architecture architecture )
    {
        this.architecture = architecture;
    }


    @Override
    public String serialize()
    {
        return MetadataUtils.JSON.toJson( this );
    }

}
