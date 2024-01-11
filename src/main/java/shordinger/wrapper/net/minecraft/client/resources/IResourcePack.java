package shordinger.wrapper.net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.resources.data.IMetadataSection;
import shordinger.wrapper.net.minecraft.client.resources.data.MetadataSerializer;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public interface IResourcePack {

    InputStream getInputStream(ResourceLocation location) throws IOException;

    boolean resourceExists(ResourceLocation location);

    Set<String> getResourceDomains();

    @Nullable
    <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName)
        throws IOException;

    BufferedImage getPackImage() throws IOException;

    String getPackName();
}
