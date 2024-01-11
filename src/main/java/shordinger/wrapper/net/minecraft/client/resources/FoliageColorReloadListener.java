package shordinger.wrapper.net.minecraft.client.resources;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureUtil;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.ColorizerFoliage;

@SideOnly(Side.CLIENT)
public class FoliageColorReloadListener implements IResourceManagerReloadListener {

    private static final ResourceLocation LOC_FOLIAGE_PNG = new ResourceLocation("textures/colormap/foliage.png");

    public void onResourceManagerReload(IResourceManager resourceManager) {
        try {
            ColorizerFoliage.setFoliageBiomeColorizer(TextureUtil.readImageData(resourceManager, LOC_FOLIAGE_PNG));
        } catch (IOException var3) {
            ;
        }
    }
}
