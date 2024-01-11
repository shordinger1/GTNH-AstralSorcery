package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.BlockRendererDispatcher;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.Tessellator;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureMap;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import shordinger.wrapper.net.minecraft.entity.item.EntityFallingBlock;
import shordinger.wrapper.net.minecraft.util.EnumBlockRenderType;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class RenderFallingBlock extends Render<EntityFallingBlock> {

    public RenderFallingBlock(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.getBlock() != null) {
            IBlockState iblockstate = entity.getBlock();

            if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {
                World world = entity.getWorldObj();

                if (iblockstate != world.getBlockState(new BlockPos(entity))
                    && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                    this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    GlStateManager.pushMatrix();
                    GlStateManager.disableLighting();
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();

                    if (this.renderOutlines) {
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                    }

                    bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                    BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
                    GlStateManager.translate(
                        (float) (x - (double) blockpos.getX() - 0.5D),
                        (float) (y - (double) blockpos.getY()),
                        (float) (z - (double) blockpos.getZ() - 0.5D));
                    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
                        .getBlockRendererDispatcher();
                    blockrendererdispatcher.getBlockModelRenderer()
                        .renderModel(
                            world,
                            blockrendererdispatcher.getModelForState(iblockstate),
                            iblockstate,
                            blockpos,
                            bufferbuilder,
                            false,
                            MathHelper.getPositionRandom(entity.getOrigin()));
                    tessellator.draw();

                    if (this.renderOutlines) {
                        GlStateManager.disableOutlineMode();
                        GlStateManager.disableColorMaterial();
                    }

                    GlStateManager.enableLighting();
                    GlStateManager.popMatrix();
                    super.doRender(entity, x, y, z, entityYaw, partialTicks);
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityFallingBlock entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
