/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.tile.TileStructController;
import shordinger.wrapper.net.minecraft.block.BlockContainer;
import shordinger.wrapper.net.minecraft.block.material.EnumPushReaction;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumBlockRenderType;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockPortalNode
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:08
 */
public class BlockPortalNode extends BlockContainer {

    public BlockPortalNode() {
        super(Material.BARRIER);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.2F);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {}

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos,
                                    EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.IGNORE;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStructController();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStructController();
    }

}
