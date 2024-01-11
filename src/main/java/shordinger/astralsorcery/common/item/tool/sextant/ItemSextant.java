/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool.sextant;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.base.ISpecialInteractItem;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.tile.IMultiblockDependantTile;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.*;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.WorldServer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemSextant
 * Created by HellFirePvP
 * Date: 25.01.2018 / 18:42
 */
public class ItemSextant extends Item implements ISpecialInteractItem {

    public ItemSextant() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            ItemStack adv = new ItemStack(this);
            setAdvanced(adv);
            items.add(adv);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (isAdvanced(stack)) {
            tooltip.add(TextFormatting.BLUE.toString() + I18n.format("item.itemsextant.upgraded"));
        }
        SextantFinder.TargetObject to = getTarget(stack);
        if (to != null) {
            tooltip.add(
                TextFormatting.GOLD.toString()
                    + I18n.format("item.itemsextant.target." + to.getRegistryName() + ".name"));
        }
    }

    public static boolean isAdvanced(ItemStack sextantStack) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return false;
        return NBTHelper.getBoolean(NBTHelper.getPersistentData(sextantStack), "advanced", false);
    }

    public static void setAdvanced(ItemStack sextantStack) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return;
        NBTHelper.getPersistentData(sextantStack)
            .setBoolean("advanced", true);
    }

    @Nullable
    public static SextantFinder.TargetObject getTarget(ItemStack sextantStack) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return null;
        return SextantFinder.getByName(
            NBTHelper.getPersistentData(sextantStack)
                .getString("target"));
    }

    public static void setTarget(ItemStack sextantStack, SextantFinder.TargetObject target) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return;
        NBTHelper.getPersistentData(sextantStack)
            .setString("target", target.getRegistryName());
    }

    @Nullable
    public static Tuple<BlockPos, Integer> getCurrentTargetInformation(ItemStack sextantStack) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return null;
        NBTTagCompound pers = NBTHelper.getPersistentData(sextantStack);
        if (!pers.hasKey("targetPos") || !pers.hasKey("targetDim")) {
            return null;
        }
        BlockPos pos = NBTHelper.readBlockPosFromNBT(pers.getCompoundTag("targetPos"));
        Integer dim = pers.getInteger("targetDim");
        return new Tuple<>(pos, dim);
    }

    public static void setCurrentTargetInformation(ItemStack sextantStack, @Nullable BlockPos pos,
                                                   @Nullable Integer dim) {
        if (sextantStack.isEmpty() || !(sextantStack.getItem() instanceof ItemSextant)) return;
        NBTTagCompound pers = NBTHelper.getPersistentData(sextantStack);
        if (pos == null || dim == null) {
            pers.removeTag("targetPos");
            pers.removeTag("targetDim");
        } else {
            NBTHelper.setAsSubTag(pers, "targetPos", tag -> NBTHelper.writeBlockPosToNBT(pos, tag));
            pers.setInteger("targetDim", dim);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
        ItemStack held = player.getHeldItem(handIn);
        if (worldIn.isRemote && ResearchManager.clientProgress.getTierReached()
            .isThisLaterOrEqual(ProgressionTier.BASIC_CRAFT)) {
            player.openGui(AstralSorcery.instance, CommonProxy.EnumGuiId.SEXTANT.ordinal(), worldIn, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    @Override
    public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
        TileEntity te = world.getTileEntity(at);
        if (te != null && te instanceof IMultiblockDependantTile) {
            PatternBlockArray struct = ((IMultiblockDependantTile) te).getRequiredStructure();
            return struct != null;
        }
        return false;
    }

    @Override
    public boolean onRightClick(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side, EnumHand hand,
                                ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof IMultiblockDependantTile) {
            PatternBlockArray struct = ((IMultiblockDependantTile) te).getRequiredStructure();
            if (struct != null) {
                if (!struct.matches(world, pos)) {
                    if (!world.isRemote && world instanceof WorldServer
                        && entityPlayer.isCreative()
                        && entityPlayer.isSneaking()
                        && MiscUtils.isChunkLoaded(world, pos)) {
                        IBlockState current = world.getBlockState(pos);
                        struct.placeInWorld(world, pos);
                        if (!world.getBlockState(pos)
                            .equals(current)) {
                            world.setBlockState(pos, current);
                        }
                    }
                    if (world.isRemote) {
                        requestPreview(te);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    private void requestPreview(TileEntity te) {
        EffectHandler.getInstance()
            .requestStructurePreviewFor((IMultiblockDependantTile) te);
    }

}
