package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.BlockRendererDispatcher;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.OpenGlHelper;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderEnderman;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureMap;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEnderman;

@SideOnly(Side.CLIENT)
public class LayerHeldBlock implements LayerRenderer<EntityEnderman> {

    private final RenderEnderman endermanRenderer;

    public LayerHeldBlock(RenderEnderman endermanRendererIn) {
        this.endermanRenderer = endermanRendererIn;
    }

    public void doRenderLayer(EntityEnderman entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IBlockState iblockstate = entitylivingbaseIn.getHeldBlockState();

        if (iblockstate != null) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
                .getBlockRendererDispatcher();
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.6875F, -0.75F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.25F, 0.1875F, 0.25F);
            float f = 0.5F;
            GlStateManager.scale(-0.5F, -0.5F, 0.5F);
            int i = entitylivingbaseIn.getBrightnessForRender();
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.endermanRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            blockrendererdispatcher.renderBlockBrightness(iblockstate, 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
