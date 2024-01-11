package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.BlockRendererDispatcher;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartTNT;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class RenderTntMinecart extends RenderMinecart<EntityMinecartTNT> {

    public RenderTntMinecart(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    protected void renderCartContents(EntityMinecartTNT p_188319_1_, float partialTicks, IBlockState p_188319_3_) {
        int i = p_188319_1_.getFuseTicks();

        if (i > -1 && (float) i - partialTicks + 1.0F < 10.0F) {
            float f = 1.0F - ((float) i - partialTicks + 1.0F) / 10.0F;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            GlStateManager.scale(f1, f1, f1);
        }

        super.renderCartContents(p_188319_1_, partialTicks, p_188319_3_);

        if (i > -1 && i / 5 % 2 == 0) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
                .getBlockRendererDispatcher();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - ((float) i - partialTicks + 1.0F) / 100.0F) * 0.8F);
            GlStateManager.pushMatrix();
            blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.getDefaultState(), 1.0F);
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
    }
}
