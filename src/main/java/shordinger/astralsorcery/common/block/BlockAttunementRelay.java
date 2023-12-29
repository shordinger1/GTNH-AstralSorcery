/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.block.network.BlockAltar;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.array.BlockArray;
import shordinger.astralsorcery.common.tile.TileAttunementRelay;
import shordinger.astralsorcery.common.tile.base.TileInventoryBase;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.struct.BlockDiscoverer;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementRelay
 * Created by HellFirePvP
 * Date: 30.11.2016 / 13:16
 */
public class BlockAttunementRelay extends BlockContainer {

    private static final AxisAlignedBB box = new AxisAlignedBB(3F / 16F, 0, 3F / 16F, 13F / 16F, 3F / 16F, 13F / 16F);

    public BlockAttunementRelay() {
        super(Material.GLASS, MapColor.QUARTZ);
        setHardness(0.5F);
        setHarvestLevel("pickaxe", 0);
        setResistance(1.0F);
        setLightLevel(0.25F);
        setSoundType(SoundType.GLASS);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAttunementRelay();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAttunementRelay();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity inv = MiscUtils.getTileAt(worldIn, pos, TileEntity.class, true);
            if (inv != null) {
                for (EnumFacing face : EnumFacing.VALUES) {
                    IItemHandler handle = inv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
                    if (handle != null) {
                        ItemUtils.dropInventory(handle, worldIn, pos);
                        break;
                    }
                }
            }

            BlockAltar.startSearchForRelayUpdate(worldIn, pos);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        startSearchRelayLinkThreadAt(worldIn, pos, true);
    }

    public static void startSearchRelayLinkThreadAt(World world, BlockPos pos, boolean recUpdate) {
        Thread searchThread = new Thread(() -> {
            BlockPos closestAltar = null;
            double dstSqOtherRelay = Double.MAX_VALUE;
            BlockArray relaysAndAltars = BlockDiscoverer.searchForBlocksAround(
                world,
                pos,
                16,
                (world1, pos1, state1) -> state1.getBlock()
                    .equals(BlocksAS.blockAltar)
                    || state1.getBlock()
                    .equals(BlocksAS.attunementRelay));
            for (Map.Entry<BlockPos, BlockArray.BlockInformation> entry : relaysAndAltars.getPattern()
                .entrySet()) {
                if (entry.getValue().type.equals(BlocksAS.blockAltar)) {
                    if (closestAltar == null || pos.distanceSq(entry.getKey()) < pos.distanceSq(closestAltar)) {
                        closestAltar = entry.getKey();
                    }
                } else {
                    double dstSqOther = entry.getKey()
                        .distanceSq(pos);
                    if (dstSqOther < dstSqOtherRelay) {
                        dstSqOtherRelay = dstSqOther;
                    }
                }
            }

            BlockPos finalClosestAltar = closestAltar;
            double finalDstSqOtherRelay = dstSqOtherRelay;
            AstralSorcery.proxy.scheduleDelayed(() -> {
                TileAttunementRelay tar = MiscUtils.getTileAt(world, pos, TileAttunementRelay.class, true);
                if (tar != null) {
                    tar.updatePositionData(finalClosestAltar, finalDstSqOtherRelay);
                }
                if (recUpdate) {
                    BlockAltar.startSearchForRelayUpdate(world, pos);
                }
            });
        });
        searchThread.setName("AttRelay PositionFinder at " + pos.toString());
        searchThread.start();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty()) {
                TileAttunementRelay tar = MiscUtils.getTileAt(worldIn, pos, TileAttunementRelay.class, true);
                if (tar != null) {
                    TileInventoryBase.ItemHandlerTile mod = tar.getInventoryHandler();
                    if (!mod.getStackInSlot(0)
                        .isEmpty()) {
                        ItemStack stack = mod.getStackInSlot(0);
                        playerIn.inventory.placeItemBackInInventory(worldIn, stack);
                        mod.setStackInSlot(0, null);
                        tar.markForUpdate();
                    }

                    if (!worldIn.isAirBlock(pos.up())) {
                        return false;
                    }

                    mod.setStackInSlot(0, ItemUtils.copyStackWithSize(held, 1));
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
                        held.shrink(1);
                    }
                    tar.markForUpdate();
                }
            } else {
                TileAttunementRelay tar = MiscUtils.getTileAt(worldIn, pos, TileAttunementRelay.class, true);
                if (tar != null) {
                    TileInventoryBase.ItemHandlerTile mod = tar.getInventoryHandler();
                    if (!mod.getStackInSlot(0)
                        .isEmpty()) {
                        ItemStack stack = mod.getStackInSlot(0);
                        playerIn.inventory.placeItemBackInInventory(worldIn, stack);
                        mod.setStackInSlot(0, null);
                        tar.markForUpdate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}
