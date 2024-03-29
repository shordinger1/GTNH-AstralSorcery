package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.model.ModelWither;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderWither;
import shordinger.wrapper.net.minecraft.entity.boss.EntityWither;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class LayerWitherAura implements LayerRenderer<EntityWither> {

    private static final ResourceLocation WITHER_ARMOR = new ResourceLocation(
        "textures/entity/wither/wither_armor.png");
    private final RenderWither witherRenderer;
    private final ModelWither witherModel = new ModelWither(0.5F);

    public LayerWitherAura(RenderWither witherRendererIn) {
        this.witherRenderer = witherRendererIn;
    }

    public void doRenderLayer(EntityWither entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isArmored()) {
            GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
            this.witherRenderer.bindTexture(WITHER_ARMOR);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f = (float) entitylivingbaseIn.ticksExisted + partialTicks;
            float f1 = MathHelper.cos(f * 0.02F) * 3.0F;
            float f2 = f * 0.01F;
            GlStateManager.translate(f1, f2, 0.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableBlend();
            float f3 = 0.5F;
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            this.witherModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.witherModel.setModelAttributes(this.witherRenderer.getMainModel());
            Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
            this.witherModel
                .render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
