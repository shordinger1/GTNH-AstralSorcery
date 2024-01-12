package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.model.ModelIronGolem;
import shordinger.wrapper.net.minecraft.client.renderer.BlockRendererDispatcher;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.OpenGlHelper;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderIronGolem;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureMap;
import shordinger.wrapper.net.minecraft.entity.monster.EntityIronGolem;
import shordinger.wrapper.net.minecraft.init.Blocks;

@SideOnly(Side.CLIENT)
public class LayerIronGolemFlower implements LayerRenderer<EntityIronGolem> {

    private final RenderIronGolem ironGolemRenderer;

    public LayerIronGolemFlower(RenderIronGolem ironGolemRendererIn) {
        this.ironGolemRenderer = ironGolemRendererIn;
    }

    public void doRenderLayer(EntityIronGolem entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.getHoldRoseTick() != 0) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
                .getBlockRendererDispatcher();
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(
                5.0F + 180.0F * ((ModelIronGolem) this.ironGolemRenderer.getMainModel()).ironGolemRightArm.rotateAngleX
                    / (float) Math.PI,
                1.0F,
                0.0F,
                0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(-0.9375F, -0.625F, -0.9375F);
            float f = 0.5F;
            GlStateManager.scale(0.5F, -0.5F, 0.5F);
            int i = entitylivingbaseIn.getBrightnessForRender();
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.ironGolemRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            blockrendererdispatcher.renderBlockBrightness(Blocks.RED_FLOWER.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
