package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderTippedArrow extends RenderArrow<EntityTippedArrow> {

    public static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
    public static final ResourceLocation RES_TIPPED_ARROW = new ResourceLocation(
        "textures/entity/projectiles/tipped_arrow.png");

    public RenderTippedArrow(RenderManager manager) {
        super(manager);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTippedArrow entity) {
        return entity.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
    }
}
