package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelIllager;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.monster.EntityMob;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySpellcasterIllager;
import shordinger.wrapper.net.minecraft.util.EnumHandSide;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderEvoker extends RenderLiving<EntityMob> {

    private static final ResourceLocation EVOKER_ILLAGER = new ResourceLocation("textures/entity/illager/evoker.png");

    public RenderEvoker(RenderManager p_i47207_1_) {
        super(p_i47207_1_, new ModelIllager(0.0F, 0.0F, 64, 64), 0.5F);
        this.addLayer(new LayerHeldItem(this) {

            public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                                      float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                if (((EntitySpellcasterIllager) entitylivingbaseIn).isSpellcasting()) {
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
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityMob entity) {
        return EVOKER_ILLAGER;
    }

    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    protected void preRenderCallback(EntityMob entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375F;
        GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
