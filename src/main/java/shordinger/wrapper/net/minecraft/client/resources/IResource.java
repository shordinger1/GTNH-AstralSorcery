package shordinger.wrapper.net.minecraft.client.resources;

import java.io.Closeable;
import java.io.InputStream;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.resources.data.IMetadataSection;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public interface IResource extends Closeable {

    ResourceLocation getResourceLocation();

    InputStream getInputStream();

    boolean hasMetadata();

    @Nullable
    <T extends IMetadataSection> T getMetadata(String sectionName);

    String getResourcePackName();
}
