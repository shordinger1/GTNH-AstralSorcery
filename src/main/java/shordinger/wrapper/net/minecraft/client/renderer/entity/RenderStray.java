package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerStrayClothing;
import shordinger.wrapper.net.minecraft.entity.monster.AbstractSkeleton;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderStray extends RenderSkeleton {

    private static final ResourceLocation STRAY_SKELETON_TEXTURES = new ResourceLocation(
        "textures/entity/skeleton/stray.png");

    public RenderStray(RenderManager p_i47191_1_) {
        super(p_i47191_1_);
        this.addLayer(new LayerStrayClothing(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
        return STRAY_SKELETON_TEXTURES;
    }
}
