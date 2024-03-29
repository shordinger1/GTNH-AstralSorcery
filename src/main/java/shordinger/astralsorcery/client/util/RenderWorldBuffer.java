/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.WorldType;
import shordinger.wrapper.net.minecraft.world.biome.Biome;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderWorldBuffer
 * Created by HellFirePvP
 * Date: 21.06.2018 / 21:50
 */
public class RenderWorldBuffer implements IBlockAccess {

    private Map<BlockPos, IBlockState> blockRenderData = new HashMap<>();
    private Biome defaultBiome;
    private WorldType worldType;

    public RenderWorldBuffer(Biome defaultBiome, WorldType worldType, BlockArray array) {
        this.defaultBiome = defaultBiome;
        this.worldType = worldType;
        this.blockRenderData.putAll(MiscUtils.remap(array.getPattern(), (bi) -> bi.state));
    }

    public void appendBlock(IBlockState state, BlockPos offset) {
        this.blockRenderData.put(offset, state);
    }

    public void appendAll(Map<BlockPos, IBlockState> states) {
        this.blockRenderData.putAll(states);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.blockRenderData.getOrDefault(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.blockRenderData.getOrDefault(pos, Blocks.AIR.getDefaultState())
            .getBlock() == Blocks.AIR;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public Biome getBiome(BlockPos pos) {
        return defaultBiome;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public WorldType getWorldType() {
        return worldType;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return getBlockState(pos).isSideSolid(this, pos, side);
    }

}
