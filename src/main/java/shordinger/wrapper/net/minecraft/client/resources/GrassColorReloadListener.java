package shordinger.wrapper.net.minecraft.client.resources;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureUtil;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.ColorizerGrass;

@SideOnly(Side.CLIENT)
public class GrassColorReloadListener implements IResourceManagerReloadListener {

    private static final ResourceLocation LOC_GRASS_PNG = new ResourceLocation("textures/colormap/grass.png");

    public void onResourceManagerReload(IResourceManager resourceManager) {
        try {
            ColorizerGrass.setGrassBiomeColorizer(TextureUtil.readImageData(resourceManager, LOC_GRASS_PNG));
        } catch (IOException var3) {
            ;
        }
    }
}
