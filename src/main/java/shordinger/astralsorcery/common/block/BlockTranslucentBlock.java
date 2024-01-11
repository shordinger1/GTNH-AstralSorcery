/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.tile.TileTranslucent;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockContainer;
import shordinger.wrapper.net.minecraft.block.SoundType;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.particle.ParticleManager;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.*;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTranslucentBlock
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:44
 */
public class BlockTranslucentBlock extends BlockContainer {

    public BlockTranslucentBlock() {
        super(Material.BARRIER);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.6F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState fst = getFakedStateTile(world, pos);
        if(fst != null) {
            RenderingUtils.playBlockBreakParticles(pos, fst);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if(rand.nextInt(30) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat(),
                    pos.getY() + rand.nextFloat(),
                    pos.getZ() + rand.nextFloat());
            p.motion(0, 0, 0);
            p.scale(0.45F).setColor(Color.WHITE).setMaxAge(65);
        }
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        IBlockState fst = getFakedStateTile(world, pos);
        if(fst != null) {
            return fst.getBlock().getSoundType(fst, world, pos, entity);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState fst = getFakedStateTile(worldIn, pos);
        if(fst != null) {
            Block actual = fst.getBlock();
            try {
                return actual.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
            } catch (Exception exc) {}
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {}

    private IBlockState getFakedStateTile(World world, BlockPos pos) {
        TileTranslucent tt = MiscUtils.getTileAt(world, pos, TileTranslucent.class, true);
        if(tt == null) return null;
        return tt.getFakedState();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        IBlockState fst = getFakedStateTile(world, pos);
        try {
            if(fst != null) {
                return fst.getBlock().getPickBlock(fst, target, world, pos, player);
            }
        } catch (Exception ignored) {}
        return ItemStack.EMPTY;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileTranslucent();
    }
}
