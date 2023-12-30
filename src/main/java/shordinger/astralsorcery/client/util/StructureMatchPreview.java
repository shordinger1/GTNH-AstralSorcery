/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import shordinger.astralsorcery.migration.BufferBuilder;
import com.gtnewhorizons.modularui.api.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import shordinger.astralsorcery.migration.DefaultVertexFormats;
import net.minecraft.init.Biomes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;

import org.lwjgl.opengl.GL11;

import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.tile.IMultiblockDependantTile;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatchPreview
 * Created by HellFirePvP
 * Date: 03.11.2017 / 09:42
 */
public class StructureMatchPreview {

    private final IMultiblockDependantTile tile;
    private int timeout;

    public StructureMatchPreview(IMultiblockDependantTile tile) {
        this.tile = tile;
        this.timeout = 100;
    }

    public void tick() {
        PatternBlockArray pattern = tile.getRequiredStructure();
        if (pattern != null && Minecraft.getMinecraft().thePlayer != null) {
            BlockPos at = tile.getLocationPos();
            BlockPos v = pattern.getSize();
            int maxDim = Math.max(Math.max(v.getX(), v.getY()), v.getZ());
            maxDim = Math.max(9, maxDim);
            if (Minecraft.getMinecraft().thePlayer.getDistance(at.getX(), at.getY(), at.getZ()) <= maxDim) {
                resetTimeout();
                return;
            }
        }
        timeout--;
    }

    public void resetTimeout() {
        this.timeout = 300;
    }

    @Nullable
    public Integer getPreviewSlice() {
        PatternBlockArray pattern = tile.getRequiredStructure();
        World world = Minecraft.getMinecraft().theWorld;
        if (pattern == null || world == null) {
            return null;
        }
        int minY = pattern.getMin()
            .getY();
        for (int y = minY; y <= pattern.getMax()
            .getY(); y++) {
            if (!pattern.matchesSlice(world, tile.getLocationPos(), y)) {
                return y;
            }
        }
        return null;
    }

    public boolean shouldBeRemoved() {
        return timeout <= 0 || tile.getRequiredStructure() == null
            || Minecraft.getMinecraft().theWorld == null
            || Minecraft.getMinecraft().theWorld.provider.dimensionId
            != ((TileEntity) tile).getWorld().provider.dimensionId
            || tile.getRequiredStructure()
            .matches(Minecraft.getMinecraft().theWorld, ((TileEntity) tile).getPos())
            || ((TileEntity) tile).isInvalid();
    }

    public boolean isOriginatingFrom(IMultiblockDependantTile tile) {
        if (!(tile instanceof TileEntity)) return false;
        if (shouldBeRemoved()) return false;
        return ((TileEntity) this.tile).getPos()
            .equals(((TileEntity) tile).getPos());
    }

    public void renderPreview(float partialTicks) {
        PatternBlockArray pba = tile.getRequiredStructure();
        World world = Minecraft.getMinecraft().theWorld;
        Integer slice = getPreviewSlice();
        if (shouldBeRemoved() || pba == null || slice == null || world == null) {
            return;
        }

        BlockPos center = tile.getLocationPos();

        IBlockAccess airWorld = new AirBlockRenderWorld(Biomes.PLAINS, WorldType.DEBUG_ALL_BLOCK_STATES);
        Tessellator tes = Tessellator.instance;
        BufferBuilder vb = tes.getBuffer();

        TextureHelper.setActiveTextureToAtlasSprite();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1F);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        GlStateManager.pushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);
        GlStateManager.translate(center.getX(), center.getY(), center.getZ());

        for (Map.Entry<BlockPos, BlockArray.BlockInformation> patternEntry : pba.getPatternSlice(slice)
            .entrySet()) {
            BlockPos offset = patternEntry.getKey();
            BlockArray.BlockInformation info = patternEntry.getValue();

            if (offset.equals(BlockPos.ORIGIN) || pba.matchSingleBlock(world, center, offset)) {
                continue;
            }

            IBlockState state = WorldHelper.getBlockState(world, center.add(offset));

            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            GlStateManager.pushMatrix();
            GlStateManager.translate(offset.getX(), offset.getY(), offset.getZ());
            GlStateManager.translate(0.125, 0.125, 0.125);
            GlStateManager.scale(0.75, 0.75, 0.75);

            if (state.getBlock()
                .isAir(state, world, center.add(offset))) {
                RenderingUtils.renderBlockSafely(airWorld, BlockPos.ORIGIN, info.state, vb);
            } else {
                RenderingUtils.renderBlockSafelyWithOptionalColor(airWorld, BlockPos.ORIGIN, info.state, vb, 16711680);
            }

            tes.draw();
            GlStateManager.popMatrix();
        }

        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        TextureHelper.refreshTextureBindState();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

}
