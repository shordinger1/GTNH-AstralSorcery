package shordinger.wrapper.net.minecraft.client.renderer.entity.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import shordinger.wrapper.net.minecraft.client.renderer.entity.RenderSnowMan;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySnowman;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class LayerSnowmanHead implements LayerRenderer<EntitySnowman> {

    private final RenderSnowMan snowManRenderer;

    public LayerSnowmanHead(RenderSnowMan snowManRendererIn) {
        this.snowManRenderer = snowManRendererIn;
    }

    public void doRenderLayer(EntitySnowman entitylivingbaseIn, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isPumpkinEquipped()) {
            GlStateManager.pushMatrix();
            this.snowManRenderer.getMainModel().head.postRender(0.0625F);
            float f = 0.625F;
            GlStateManager.translate(0.0F, -0.34375F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.625F, -0.625F, -0.625F);
            Minecraft.getMinecraft()
                .getItemRenderer()
                .renderItem(
                    entitylivingbaseIn,
                    new ItemStack(Blocks.PUMPKIN, 1),
                    ItemCameraTransforms.TransformType.HEAD);
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
