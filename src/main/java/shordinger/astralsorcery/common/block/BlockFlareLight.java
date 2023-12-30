/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.EnumDyeColor;
import shordinger.astralsorcery.migration.block.AstralBlock;
import shordinger.astralsorcery.migration.block.BlockFaceShape;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.BlockStateContainer;
import shordinger.astralsorcery.migration.block.EnumBlockRenderType;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFlareLight
 * Created by HellFirePvP
 * Date: 22.10.2016 / 14:36
 */
public class BlockFlareLight extends AstralBlock {

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum
        .<EnumDyeColor>create("color", EnumDyeColor.class);

    public BlockFlareLight() {
        super(Material.AIR, MapColor.QUARTZ);
        setLightLevel(1F);
        setBlockUnbreakable();
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(
            this.blockState.getBaseState()
                .withProperty(COLOR, EnumDyeColor.YELLOW));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
    }

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
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COLOR)
            .getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        EntityFXFacingParticle p = EffectHelper
            .genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5)
            .gravity(0.004);
        p.offset(
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.4F + rand.nextFloat() * 0.1F)
            .setAlphaMultiplier(0.75F);
        p.motion(0, rand.nextFloat() * 0.02F, 0)
            .setMaxAge(50 + rand.nextInt(20));
        p.setColor(MiscUtils.flareColorFromDye(stateIn.getValue(COLOR)));
        p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5)
            .gravity(0.004);
        p.offset(
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.4F + rand.nextFloat() * 0.1F)
            .setAlphaMultiplier(0.75F);
        p.motion(0, rand.nextFloat() * 0.02F, 0)
            .setMaxAge(50 + rand.nextInt(20));
        p.setColor(MiscUtils.flareColorFromDye(stateIn.getValue(COLOR)));
        if (rand.nextBoolean()) {
            p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5)
                .gravity(0.004);
            p.offset(
                rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.1F + rand.nextFloat() * 0.05F)
                .setColor(Color.WHITE)
                .setMaxAge(25);
        }
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
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, ForgeDirection side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            ForgeDirection p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.IGNORE;
    }

}
