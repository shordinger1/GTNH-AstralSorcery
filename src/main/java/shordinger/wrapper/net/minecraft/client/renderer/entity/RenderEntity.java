package shordinger.wrapper.net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderEntity extends Render<Entity> {

    public RenderEntity(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        renderOffsetAABB(
            entity.getEntityBoundingBox(),
            x - entity.lastTickPosX,
            y - entity.lastTickPosY,
            z - entity.lastTickPosZ);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Nullable
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
