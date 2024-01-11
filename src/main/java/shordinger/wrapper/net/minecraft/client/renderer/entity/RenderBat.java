package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelBat;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityBat;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class RenderBat extends RenderLiving<EntityBat> {

    private static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

    public RenderBat(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBat(), 0.25F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBat entity) {
        return BAT_TEXTURES;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityBat entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.35F, 0.35F, 0.35F);
    }

    protected void applyRotations(EntityBat entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
        if (entityLiving.getIsBatHanging()) {
            GlStateManager.translate(0.0F, -0.1F, 0.0F);
        } else {
            GlStateManager.translate(0.0F, MathHelper.cos(p_77043_2_ * 0.3F) * 0.1F, 0.0F);
        }

        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
    }
}
