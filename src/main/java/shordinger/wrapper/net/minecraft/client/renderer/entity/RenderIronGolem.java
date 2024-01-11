package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelIronGolem;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import shordinger.wrapper.net.minecraft.entity.monster.EntityIronGolem;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderIronGolem extends RenderLiving<EntityIronGolem> {

    private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation("textures/entity/iron_golem.png");

    public RenderIronGolem(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelIronGolem(), 0.5F);
        this.addLayer(new LayerIronGolemFlower(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityIronGolem entity) {
        return IRON_GOLEM_TEXTURES;
    }

    protected void applyRotations(EntityIronGolem entityLiving, float p_77043_2_, float rotationYaw,
                                  float partialTicks) {
        super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);

        if ((double) entityLiving.limbSwingAmount >= 0.01D) {
            float f = 13.0F;
            float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
        }
    }
}
