/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraft.client.gui.ScaledResolution;
import shordinger.wrapper.net.minecraft.client.renderer.BufferBuilder;
import shordinger.wrapper.net.minecraft.client.renderer.GlStateManager;
import shordinger.wrapper.net.minecraft.client.renderer.Tessellator;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import shordinger.wrapper.net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.VertexFormat;
import shordinger.wrapper.net.minecraft.init.Biomes;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.Vec3i;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.WorldType;
import shordinger.wrapper.net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArrayRender
 * Created by HellFirePvP
 * Date: 30.09.2016 / 11:37
 */
public class BlockArrayRenderHelper {

    private BlockArray blocks;
    private WorldBlockArrayRenderAccess renderAccess;
    private double rotX, rotY, rotZ;

    public BlockArrayRenderHelper(BlockArray blocks) {
        this.blocks = blocks;
        this.renderAccess = new WorldBlockArrayRenderAccess(blocks);
        resetRotation();
    }

    private void resetRotation() {
        this.rotX = -30;
        this.rotY = 45;
        this.rotZ = 0;
    }

    public void rotate(double x, double y, double z) {
        this.rotX += x;
        this.rotY += y;
        this.rotZ += z;
    }

    public int getDefaultSlice() {
        return Collections.min(renderAccess.blockRenderData.keySet(), Comparator.comparing(BlockPos::getY))
            .getY();
    }

    public boolean hasSlice(int y) {
        return MiscUtils.contains(renderAccess.blockRenderData.keySet(), pos -> pos.getY() == y);
    }

    public void render3DGUI(double x, double y, float pTicks) {
        render3DSliceGUI(x, y, pTicks, Optional.empty());
    }

    public void render3DSliceGUI(double x, double y, float pTicks, Optional<Integer> slice) {
        GuiScreen scr = Minecraft.getMinecraft().currentScreen;
        if (scr == null) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        double sc = new ScaledResolution(mc).getScaleFactor();
        GlStateManager.translate(x + 16D / sc, y + 16D / sc, 512);

        double mul = 10.5;

        double size = 2;
        double minSize = 0.5;

        Vec3i max = blocks.getMax();
        Vec3i min = blocks.getMin();

        double maxLength = 0;
        double pointDst = max.getX() - min.getX();
        if (pointDst > maxLength) maxLength = pointDst;
        pointDst = max.getY() - min.getY();
        if (pointDst > maxLength) maxLength = pointDst;
        pointDst = max.getZ() - min.getZ();
        if (pointDst > maxLength) maxLength = pointDst;
        maxLength -= 5;

        if (maxLength > 0) {
            size = (size - minSize) * (1D - (maxLength / 20D));
        }

        double dr = -5.75 * size;
        GlStateManager.translate(dr, dr, dr);
        GlStateManager.rotate((float) rotX, 1, 0, 0);
        GlStateManager.rotate((float) rotY, 0, 1, 0);
        GlStateManager.rotate((float) rotZ, 0, 0, 1);
        GlStateManager.translate(-dr, -dr, -dr);

        GlStateManager.scale(-size * mul, -size * mul, -size * mul);

        VertexFormat blockFormat = DefaultVertexFormats.BLOCK;

        TextureHelper.setActiveTextureToAtlasSprite();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        Set<Map.Entry<BlockPos, BakedBlockData>> renderArray = renderAccess.blockRenderData.entrySet();
        renderAccess.slice = slice;
        slice.ifPresent(ySlice -> GlStateManager.translate(0, -ySlice, 0));

        vb.begin(GL11.GL_QUADS, blockFormat);
        for (Map.Entry<BlockPos, BakedBlockData> data : renderArray) {
            BlockPos offset = data.getKey();
            if (slice.isPresent()) {
                if (offset.getY() != slice.get()) {
                    continue;
                }
            }
            BakedBlockData renderData = data.getValue();
            if (renderData.tileEntity != null) {
                renderData.tileEntity.setWorld(Minecraft.getMinecraft().theWorld);
                renderData.tileEntity.setPos(offset);
            }
            if (renderData.type != Blocks.AIR) {
                RenderingUtils.renderBlockSafely(renderAccess, offset, renderData.state, vb);
            }
        }
        tes.draw();

        for (Map.Entry<BlockPos, BakedBlockData> data : renderArray) {
            BlockPos offset = data.getKey();
            if (slice.isPresent()) {
                if (offset.getY() != slice.get()) {
                    continue;
                }
            }
            BakedBlockData renderData = data.getValue();
            if (renderData.tileEntity != null && renderData.tesr != null) {
                renderData.tileEntity.setWorld(Minecraft.getMinecraft().theWorld);
                renderData.tileEntity.setPos(offset);
                renderData.tesr
                    .render(renderData.tileEntity, offset.getX(), offset.getY(), offset.getZ(), pTicks, 0, 1F);
            }
        }
        renderAccess.slice = Optional.empty();

        GlStateManager.popMatrix();
        GL11.glPopAttrib();
    }

    public static class BakedBlockData extends BlockArray.BlockInformation {

        private TileEntity tileEntity;
        private TileEntitySpecialRenderer tesr;

        protected BakedBlockData(Block type, IBlockState state, TileEntity te) {
            super(type, state);
            this.tileEntity = te;
            if (te != null) {
                tesr = TileEntityRendererDispatcher.instance.getRenderer(te);
            }
        }

    }

    public static class WorldBlockArrayRenderAccess implements IBlockAccess {

        private Map<BlockPos, BakedBlockData> blockRenderData = new HashMap<>();
        private Optional<Integer> slice = Optional.empty();

        public WorldBlockArrayRenderAccess(BlockArray array) {
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : array.getPattern()
                .entrySet()) {
                BlockPos offset = entry.getKey();
                BlockArray.BlockInformation info = entry.getValue();
                if (info.type.hasTileEntity(info.state)) {
                    TileEntity te = info.type.createTileEntity(Minecraft.getMinecraft().theWorld, info.state);
                    BlockArray.TileEntityCallback callback = array.getTileCallbacks()
                        .get(offset);
                    if (te != null && callback != null) {
                        if (callback.isApplicable(te)) {
                            callback.onPlace(this, offset, te);
                        }
                    }
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.state, te));
                } else {
                    blockRenderData.put(offset, new BakedBlockData(info.type, info.state, null));
                }
            }
        }

        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return isInBounds(pos) ? blockRenderData.get(pos).tileEntity : null;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 0;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return isInBounds(pos) ? blockRenderData.get(pos).state : Blocks.AIR.getDefaultState();
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return !isInBounds(pos) || blockRenderData.get(pos).type == Blocks.AIR;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        private boolean isInBounds(BlockPos pos) {
            return blockRenderData.containsKey(pos) && (!slice.isPresent() || pos.getY() == slice.get());
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return 0;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public WorldType getWorldType() {
            return Minecraft.getMinecraft().theWorld.getWorldType();
        }

        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
            return isInBounds(pos) ? getBlockState(pos).isSideSolid(this, pos, side) : _default;
        }

    }

}
