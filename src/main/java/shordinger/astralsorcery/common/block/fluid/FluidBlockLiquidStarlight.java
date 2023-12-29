/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.block.BlockCustomSandOre;
import shordinger.astralsorcery.common.block.BlockInfusedWood;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidBlockLiquidStarlight
 * Created by HellFirePvP
 * Date: 14.09.2016 / 11:38
 */
public class FluidBlockLiquidStarlight extends BlockFluidClassic {

    public FluidBlockLiquidStarlight() {
        super(BlocksAS.fluidLiquidStarlight, new MaterialLiquid(MapColor.SILVER));
        setDefaultState(
            this.blockState.getBaseState()
                .withProperty(LEVEL, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        Integer level = stateIn.getValue(LEVEL);
        double percHeight = 1D - (((double) level + 1) / 8D);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        p.offset(0, percHeight, 0);
        p.offset(
            rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1),
            0,
            rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.2F)
            .gravity(0.006)
            .setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        if (rand.nextInt(3) == 0) {
            p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            p.offset(0, percHeight, 0);
            p.offset(
                rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1),
                0,
                rand.nextFloat() * 0.5 * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.2F)
                .gravity(0.006)
                .setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        }
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos,
                                @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos) {
        super.neighborChanged(state, world, pos, neighborBlock, neighbourPos);

        interactWithAdjacent(world, pos);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);

        interactWithAdjacent(world, pos);
    }

    private void interactWithAdjacent(World world, BlockPos pos) {
        boolean shouldCreateBlock = false;
        boolean isCold = true;

        for (EnumFacing side : EnumFacing.VALUES) {
            if (side != EnumFacing.DOWN) {
                IBlockState offset = WorldHelper.getBlockState(world, pos.offset(side));
                if (offset.getMaterial()
                    .isLiquid() && !(offset.getBlock() instanceof FluidBlockLiquidStarlight)
                    && (offset.getBlock() instanceof BlockFluidBase || offset.getBlock() instanceof BlockLiquid)) {
                    int temp = offset.getBlock() instanceof BlockFluidBase
                        ? BlockFluidBase.getTemperature(world, pos.offset(side))
                        : (offset.getMaterial() == Material.LAVA ? FluidRegistry.LAVA.getTemperature()
                        : offset.getMaterial() == Material.WATER ? FluidRegistry.WATER.getTemperature() : 100);
                    isCold = temp <= 300; // colder or equals water.
                    shouldCreateBlock = true;
                    break;
                }
            }
        }

        if (shouldCreateBlock) {
            if (isCold) {
                if (Config.liquidStarlightIce) {
                    world.setBlockState(pos, Blocks.ICE.getDefaultState());
                } else {
                    world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
                }
            } else {
                if (Config.liquidStarlightSand) {
                    if (Config.liquidStarlightAquamarine && world.rand.nextInt(900) == 0) {
                        world.setBlockState(
                            pos,
                            BlocksAS.customSandOre.getDefaultState()
                                .withProperty(BlockCustomSandOre.ORE_TYPE, BlockCustomSandOre.OreType.AQUAMARINE));
                    } else {
                        world.setBlockState(pos, Blocks.SAND.getDefaultState());
                    }
                } else {
                    world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
                }
            }

            world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_STONE_BREAK,
                SoundCategory.BLOCKS,
                0.5F,
                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
            for (int i = 0; i < 10; ++i) {
                world.spawnParticle(
                    EnumParticleTypes.SMOKE_LARGE,
                    pos.getX() + Math.random(),
                    pos.getY() + Math.random(),
                    pos.getZ() + Math.random(),
                    0.0D,
                    0.0D,
                    0.0D);
            }
        }

    }

    @Override
    public boolean displaceIfPossible(World world, BlockPos pos) {
        return !WorldHelper.getBlockState(world, pos)
            .getMaterial()
            .isLiquid() && super.displaceIfPossible(world, pos);
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity,
                                          double yToTest, Material materialIn, boolean testingHead) {
        AxisAlignedBB box = iblockstate.getBoundingBox(world, blockpos)
            .offset(blockpos);
        AxisAlignedBB entityBox = entity.getEntityBoundingBox();// .offset(entity.posX, entity.posY, entity.posZ);
        return box.intersects(entityBox) && materialIn.isLiquid();
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

        if (entityIn instanceof EntityPlayer) {
            ((EntityPlayer) entityIn).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true));
        } else if (entityIn instanceof EntityItem) {
            ItemStack contained = ((EntityItem) entityIn).getItem();
            if (!contained.isEmpty()) {
                if (entityIn.getEntityWorld().isRemote) return;

                if (Config.liquidStarlightInfusedWood) {
                    interactInfusedWood(contained, entityIn);
                }
            }
        }
    }

    private void interactInfusedWood(ItemStack contained, Entity entityIn) {
        if (ItemUtils.hasOreName(contained, "logWood")) {
            contained = ItemUtils.copyStackWithSize(contained, contained.getCount() - 1);
            if (contained.isEmpty()) {
                entityIn.setDead();
            } else {
                ((EntityItem) entityIn).setItem(contained);
            }
            ItemUtils.dropItemNaturally(
                entityIn.getEntityWorld(),
                entityIn.posX,
                entityIn.posY,
                entityIn.posZ,
                BlockInfusedWood.WoodType.RAW.asStack());
        }
    }
}
