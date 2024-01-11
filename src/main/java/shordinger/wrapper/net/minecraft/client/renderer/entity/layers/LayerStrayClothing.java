package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.model.ModelSkeleton;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderLivingBase;
import shordinger.wrapper.net.minecraft.entity.monster.EntityStray;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class LayerStrayClothing implements LayerRenderer<EntityStray> {

    private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation(
        "textures/entity/skeleton/stray_overlay.png");
    private final RenderLivingBase<?> renderer;
    private final ModelSkeleton layerModel = new ModelSkeleton(0.25F, true);

    public LayerStrayClothing(RenderLivingBase<?> p_i47183_1_) {
        this.renderer = p_i47183_1_;
    }

    public void doRenderLayer(EntityStray entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.layerModel.setModelAttributes(this.renderer.getMainModel());
        this.layerModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderer.bindTexture(STRAY_CLOTHES_TEXTURES);
        this.layerModel
            .render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
