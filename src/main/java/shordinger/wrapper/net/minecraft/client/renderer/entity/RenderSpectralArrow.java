package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySpectralArrow;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderSpectralArrow extends RenderArrow<EntitySpectralArrow> {

    public static final ResourceLocation RES_SPECTRAL_ARROW = new ResourceLocation(
        "textures/entity/projectiles/spectral_arrow.png");

    public RenderSpectralArrow(RenderManager manager) {
        super(manager);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySpectralArrow entity) {
        return RES_SPECTRAL_ARROW;
    }
}
