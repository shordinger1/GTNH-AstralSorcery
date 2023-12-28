/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.TileWell;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:43
 */
public class BlockWell extends BlockStarlightNetwork {

    private static final AxisAlignedBB boxWell = new AxisAlignedBB(1D / 16D, 0D, 1D / 16D, 15D / 16D, 1, 15D / 16D);
    private static List<AxisAlignedBB> collisionBoxes;

    public BlockWell() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
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
        return new TileWell();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return p_193383_4_ == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileWell tw = MiscUtils.getTileAt(world, pos, TileWell.class, true);
        if (tw != null) {
            if (tw.getHeldFluid() != null) {
                return tw.getHeldFluid()
                    .getLuminosity();
            }
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {

            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (!heldItem.isEmpty() && playerIn instanceof EntityPlayerMP) {
                TileWell tw = MiscUtils.getTileAt(worldIn, pos, TileWell.class, false);
                if (tw == null) return false;

                WellLiquefaction.LiquefactionEntry entry = WellLiquefaction.getLiquefactionEntry(heldItem);
                if (entry != null) {
                    ItemStackHandler handle = tw.getInventoryHandler();
                    if (!handle.getStackInSlot(0)
                        .isEmpty()) return false;

                    if (!worldIn.isAirBlock(pos.up())) {
                        return false;
                    }

                    handle.setStackInSlot(0, ItemUtils.copyStackWithSize(heldItem, 1));
                    worldIn.playSound(
                        null,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP,
                        SoundCategory.PLAYERS,
                        0.2F,
                        ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

                    if (!playerIn.isCreative()) {
                        heldItem.shrink(1);
                    }
                    if (heldItem.getCount() <= 0) {
                        playerIn.setHeldItem(hand, null);
                    }
                }

                FluidActionResult far = FluidUtil.tryFillContainerAndStow(
                    heldItem,
                    tw.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN),
                    new InvWrapper(playerIn.inventory),
                    Fluid.BUCKET_VOLUME,
                    playerIn,
                    true);
                if (far.isSuccess()) {
                    playerIn.setHeldItem(hand, far.getResult());
                    SoundHelper.playSoundAround(SoundEvents.ITEM_BUCKET_FILL, worldIn, pos, 1F, 1F);
                    tw.markForUpdate();
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileWell tw = MiscUtils.getTileAt(worldIn, pos, TileWell.class, true);
        if (tw != null && !worldIn.isRemote) {
            ItemStack stack = tw.getInventoryHandler()
                .getStackInSlot(0);
            if (!stack.isEmpty()) {
                tw.breakCatalyst();
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.UP;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        for (AxisAlignedBB box : collisionBoxes) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileWell tw = MiscUtils.getTileAt(worldIn, pos, TileWell.class, false);
        if (tw != null) {
            return MathHelper.ceil(tw.getPercFilled() * 15F);
        }
        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boxWell;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    static {
        List<AxisAlignedBB> boxes = new LinkedList<>();

        boxes.add(new AxisAlignedBB(1D / 16D, 0D, 1D / 16D, 15D / 16D, 5D / 16D, 15D / 16D));

        boxes.add(new AxisAlignedBB(1D / 16D, 5D / 16D, 1D / 16D, 2D / 16D, 1D, 15D / 16D));
        boxes.add(new AxisAlignedBB(1D / 16D, 5D / 16D, 1D / 16D, 15D / 16D, 1D, 2D / 16D));
        boxes.add(new AxisAlignedBB(14D / 16D, 5D / 16D, 1D / 16D, 15D / 16D, 1D, 15D / 16D));
        boxes.add(new AxisAlignedBB(1D / 16D, 5D / 16D, 14D / 16D, 15D / 16D, 1D, 15D / 16D));

        collisionBoxes = Collections.unmodifiableList(boxes);
    }

}
