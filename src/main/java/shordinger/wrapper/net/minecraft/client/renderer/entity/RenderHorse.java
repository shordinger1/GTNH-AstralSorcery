package shordinger.wrapper.net.minecraft.client.renderer.entity;

import java.util.Map;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.model.ModelHorse;
import shordinger.wrapper.net.minecraft.client.renderer.texture.LayeredTexture;
import shordinger.wrapper.net.minecraft.entity.passive.EntityHorse;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderHorse extends RenderLiving<EntityHorse> {

    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps
        .<String, ResourceLocation>newHashMap();

    public RenderHorse(RenderManager p_i47205_1_) {
        super(p_i47205_1_, new ModelHorse(), 0.75F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityHorse entity) {
        String s = entity.getHorseTexture();
        ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s);
            Minecraft.getMinecraft()
                .getTextureManager()
                .loadTexture(resourcelocation, new LayeredTexture(entity.getVariantTexturePaths()));
            LAYERED_LOCATION_CACHE.put(s, resourcelocation);
        }

        return resourcelocation;
    }
}
