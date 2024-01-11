package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelIllager;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.monster.EntityMob;
import shordinger.wrapper.net.minecraft.entity.monster.EntityVindicator;
import shordinger.wrapper.net.minecraft.util.EnumHandSide;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderVindicator extends RenderLiving<EntityMob> {

    private static final ResourceLocation VINDICATOR_TEXTURE = new ResourceLocation(
        "textures/entity/illager/vindicator.png");

    public RenderVindicator(RenderManager p_i47189_1_) {
        super(p_i47189_1_, new ModelIllager(0.0F, 0.0F, 64, 64), 0.5F);
        this.addLayer(new LayerHeldItem(this) {

            public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                                      float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (((EntityVindicator) entitylivingbaseIn).isAggressive()) {
                    super.doRenderLayer(
                        entitylivingbaseIn,
                        limbSwing,
                        limbSwingAmount,
                        partialTicks,
                        ageInTicks,
                        netHeadYaw,
                        headPitch,
                        scale);
                }
            }

            protected void translateToHand(EnumHandSide p_191361_1_) {
                ((ModelIllager) this.livingEntityRenderer.getMainModel()).getArm(p_191361_1_)
                    .postRender(0.0625F);
            }
        });
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityMob entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityMob entity) {
        return VINDICATOR_TEXTURE;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityMob entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375F;
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
