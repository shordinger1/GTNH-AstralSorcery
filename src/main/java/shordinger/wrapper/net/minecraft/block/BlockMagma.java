package shordinger.wrapper.net.minecraft.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.enchantment.EnchantmentHelper;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;

public class BlockMagma extends Block {

    public BlockMagma() {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setLightLevel(0.2F);
        this.setTickRandomly(true);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public net.minecraft.block.material.MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.NETHERRACK;
    }

    /**
     * Called when the given entity walks on this Block
     */
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire() && entityIn instanceof EntityLivingBase
            && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) entityIn)) {
            entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }

        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
        return 15728880;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        BlockPos blockpos = pos.up();
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() == Blocks.WATER || iblockstate.getBlock() == Blocks.FLOWING_WATER) {
            worldIn.setBlockToAir(blockpos);
            worldIn.playSound(
                (EntityPlayer) null,
                pos,
                SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS,
                0.5F,
                2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

            if (worldIn instanceof WorldServer) {
                ((WorldServer) worldIn).spawnParticle(
                    EnumParticleTypes.SMOKE_LARGE,
                    (double) blockpos.getX() + 0.5D,
                    (double) blockpos.getY() + 0.25D,
                    (double) blockpos.getZ() + 0.5D,
                    8,
                    0.5D,
                    0.25D,
                    0.5D,
                    0.0D);
            }
        }
    }

    public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
        return entityIn.isImmuneToFire();
    }
}
