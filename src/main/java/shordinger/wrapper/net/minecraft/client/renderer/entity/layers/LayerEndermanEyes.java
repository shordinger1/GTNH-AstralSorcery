package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.OpenGlHelper;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderEnderman;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEnderman;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class LayerEndermanEyes implements LayerRenderer<EntityEnderman> {

    private static final ResourceLocation RES_ENDERMAN_EYES = new ResourceLocation(
        "textures/entity/enderman/enderman_eyes.png");
    private final RenderEnderman endermanRenderer;

    public LayerEndermanEyes(RenderEnderman endermanRendererIn) {
        this.endermanRenderer = endermanRendererIn;
    }

    public void doRenderLayer(EntityEnderman entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
        int i = 61680;
        int j = 61680;
        int k = 0;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        this.endermanRenderer.getMainModel()
            .render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        this.endermanRenderer.setLightmap(entitylivingbaseIn);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
