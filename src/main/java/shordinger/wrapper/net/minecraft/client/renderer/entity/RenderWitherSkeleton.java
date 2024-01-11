package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.monster.AbstractSkeleton;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderWitherSkeleton extends RenderSkeleton {

    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation(
        "textures/entity/skeleton/wither_skeleton.png");

    public RenderWitherSkeleton(RenderManager p_i47188_1_) {
        super(p_i47188_1_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
        return WITHER_SKELETON_TEXTURES;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(AbstractSkeleton entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(1.2F, 1.2F, 1.2F);
    }
}
