/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.wand;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.block.BlockFlareLight;
import shordinger.astralsorcery.common.block.BlockTranslucentBlock;
import shordinger.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import shordinger.astralsorcery.common.item.base.render.ItemDynamicColor;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.TileIlluminator;
import shordinger.astralsorcery.common.tile.TileTranslucent;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.block.Block;

import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.EnumDyeColor;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationWand
 * Created by HellFirePvP
 * Date: 17.01.2017 / 15:09
 */
public class ItemIlluminationWand extends Item implements ItemAlignmentChargeConsumer, ItemDynamicColor {

    public ItemIlluminationWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        EnumDyeColor color = getConfiguredColor(stack);
        if (color != null) {
            tooltip.add(
                MiscUtils.textFormattingForDye(color)
                    + MiscUtils.capitalizeFirst(I18n.format(color.getUnlocalizedName())));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFF;
        EnumDyeColor color = getConfiguredColor(stack);
        if (color == null) color = EnumDyeColor.YELLOW;
        Color c = MiscUtils.flareColorFromDye(color);
        return 0xFF000000 | c.getRGB();
    }

    public static void setConfiguredColor(ItemStack stack, EnumDyeColor color) {
        NBTHelper.getPersistentData(stack)
            .setInteger("color", color.getDyeDamage());
    }

    @Nullable
    public static EnumDyeColor getConfiguredColor(ItemStack stack) {
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (tag != null && tag.hasKey("color")) {
            return EnumDyeColor.byDyeDamage(
                NBTHelper.getPersistentData(stack)
                    .getInteger("color"));
        }
        return null;
    }

    public static IBlockState getPlacingState(ItemStack wand) {
        EnumDyeColor config = getConfiguredColor(wand);
        if (config != null) {
            return BlocksAS.blockVolatileLight.getDefaultState()
                .withProperty(BlockFlareLight.COLOR, config);
        }
        return BlocksAS.blockVolatileLight.getDefaultState();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemIlluminationWand)) {
            return EnumActionResult.SUCCESS;
        }
        if (!worldIn.isRemote) {
            IBlockState at = worldIn.getBlockState(pos);
            if (!playerIn.isSneaking()) {
                TileIlluminator illum = MiscUtils.getTileAt(worldIn, pos, TileIlluminator.class, false);
                if (illum != null) {
                    EnumDyeColor thisColor = getConfiguredColor(stack);
                    if (thisColor == null) {
                        thisColor = EnumDyeColor.YELLOW;
                    }
                    illum.onWandUsed(thisColor);
                    drainTempCharge(playerIn, PlayerChargeHandler.INSTANCE.getCharge(playerIn), false);
                } else {
                    IBlockState iblockstate = worldIn.getBlockState(pos);
                    Block block = iblockstate.getBlock();
                    if (!block.isReplaceable(worldIn, pos)) {
                        pos = pos.offset(facing);
                    }
                    if (playerIn.canPlayerEdit(pos, facing, stack)) {
                        if (worldIn.getBlockState(pos)
                            .equals(getPlacingState(stack))) {
                            SoundType soundtype = (SoundType) worldIn.getBlockState(pos)
                                .getBlock()
                                .getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                            if (worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3)) {
                                worldIn.playSound(
                                    playerIn,
                                    pos,
                                    soundtype.getPlaceSound(),
                                    SoundCategory.BLOCKS,
                                    (soundtype.getVolume() + 1.0F) / 2.0F,
                                    soundtype.getPitch() * 0.8F);
                            }
                        } else if (worldIn.mayPlace(BlocksAS.blockVolatileLight, pos, true, facing, null)
                            && drainTempCharge(playerIn, Config.illuminationWandUseCost, true)) {
                            if (worldIn.setBlockState(pos, getPlacingState(stack), 3)) {
                                SoundType soundtype = (SoundType) worldIn.getBlockState(pos)
                                    .getBlock()
                                    .getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                                worldIn.playSound(
                                    playerIn,
                                    pos,
                                    soundtype.getPlaceSound(),
                                    SoundCategory.BLOCKS,
                                    (soundtype.getVolume() + 1.0F) / 2.0F,
                                    soundtype.getPitch() * 0.8F);
                                drainTempCharge(playerIn, Config.illuminationWandUseCost, false);
                            }
                        }
                    }
                }
            } else {
                if (at.isNormalCube()) {
                    TileEntity te = worldIn.getTileEntity(pos);
                    if (te == null && !at.getBlock()
                        .hasTileEntity(at) && drainTempCharge(playerIn, Config.illuminationWandUseCost, true)) {
                        if (worldIn.setBlockState(pos, BlocksAS.translucentBlock.getDefaultState(), 3)) {
                            TileTranslucent tt = MiscUtils.getTileAt(worldIn, pos, TileTranslucent.class, true);
                            if (tt == null) {
                                worldIn.setBlockState(pos, at, 3);
                            } else {
                                tt.setFakedState(at);
                                drainTempCharge(playerIn, Config.illuminationWandUseCost, false);
                            }
                        }
                    }
                } else if (at.getBlock() instanceof BlockTranslucentBlock) {
                    TileTranslucent tt = MiscUtils.getTileAt(worldIn, pos, TileTranslucent.class, true);
                    if (tt != null && tt.getFakedState() != null) {
                        worldIn.setBlockState(pos, tt.getFakedState(), 3);
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

}
