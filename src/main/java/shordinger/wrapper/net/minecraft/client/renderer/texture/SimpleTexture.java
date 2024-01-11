package shordinger.wrapper.net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.resources.IResource;
import shordinger.wrapper.net.minecraft.client.resources.IResourceManager;
import shordinger.wrapper.net.minecraft.client.resources.data.TextureMetadataSection;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SimpleTexture extends AbstractTexture {

    private static final Logger LOGGER = LogManager.getLogger();
    protected final ResourceLocation textureLocation;

    public SimpleTexture(ResourceLocation textureResourceLocation) {
        this.textureLocation = textureResourceLocation;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        IResource iresource = null;

        try {
            iresource = resourceManager.getResource(this.textureLocation);
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
            boolean flag = false;
            boolean flag1 = false;

            if (iresource.hasMetadata()) {
                try {
                    TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource
                        .getMetadata("texture");

                    if (texturemetadatasection != null) {
                        flag = texturemetadatasection.getTextureBlur();
                        flag1 = texturemetadatasection.getTextureClamp();
                    }
                } catch (RuntimeException runtimeexception) {
                    LOGGER.warn("Failed reading metadata of: {}", this.textureLocation, runtimeexception);
                }
            }

            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
        } finally {
            IOUtils.closeQuietly((Closeable) iresource);
        }
    }
}
