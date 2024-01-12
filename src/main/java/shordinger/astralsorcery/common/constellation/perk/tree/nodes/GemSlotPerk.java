/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes;

import java.util.Collection;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.gem.ItemPerkGem;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemSlotPerk
 * Created by HellFirePvP
 * Date: 17.11.2018 / 18:55
 */
public interface GemSlotPerk {

    public static final String SOCKET_DATA_KEY = "socketedItem";

    default public boolean hasItem(EntityPlayer player, Side side) {
        return hasItem(player, side, null);
    }

    default public boolean hasItem(EntityPlayer player, Side side, @Nullable NBTTagCompound data) {
        return !getContainedItem(player, side, data).isEmpty();
    }

    default public ItemStack getContainedItem(EntityPlayer player, Side side) {
        return getContainedItem(player, side, null);
    }

    default public ItemStack getContainedItem(EntityPlayer player, Side side, @Nullable NBTTagCompound dataOvr) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException(
                "Cannot do perk-specific socketing logic on something that's not a perk!");
        }
        NBTTagCompound data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return ItemStack.EMPTY;
        }

        return NBTHelper.getStack(data, SOCKET_DATA_KEY);
    }

    default public boolean setContainedItem(EntityPlayer player, Side side, ItemStack stack) {
        return setContainedItem(player, side, null, stack);
    }

    default public boolean setContainedItem(EntityPlayer player, Side side, @Nullable NBTTagCompound dataOvr,
                                            ItemStack stack) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException(
                "Cannot do perk-specific socketing logic on something that's not a perk!");
        }
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (!prog.hasPerkUnlocked((AbstractPerk) this)) {
            return false;
        }
        boolean updateData = dataOvr == null;
        NBTTagCompound data = dataOvr != null ? dataOvr : ((AbstractPerk) this).getPerkData(player, side);
        if (data == null) {
            return false;
        }
        NBTTagCompound prev = data.copy();

        if (stack.isEmpty()) {
            data.removeTag(SOCKET_DATA_KEY);
        } else {
            NBTHelper.setStack(data, SOCKET_DATA_KEY, stack);
        }

        if (updateData) {
            ResearchManager.setPerkData(player, (AbstractPerk) this, prev, data);
        }
        return true;
    }

    default public void dropItemToPlayer(EntityPlayer player) {
        dropItemToPlayer(player, null);
    }

    default public void dropItemToPlayer(EntityPlayer player, @Nullable NBTTagCompound data) {
        if (!(this instanceof AbstractPerk)) {
            throw new UnsupportedOperationException(
                "Cannot do perk-specific socketing logic on something that's not a perk!");
        }

        if (player.getEntityWorld().isRemote) {
            return;
        }

        boolean updateData = data == null;
        if (updateData) {
            data = ((AbstractPerk) this).getPerkData(player, Side.SERVER);
        }
        if (data == null) {
            return;
        }
        NBTTagCompound prev = data.copy();

        ItemStack contained = getContainedItem(player, Side.SERVER, data);
        if (!contained.isEmpty()) {
            if (!player.addItemStackToInventory(contained)) {
                ItemUtils.dropItem(player.world, player.posX, player.posY, player.posZ, contained);
            }
        }
        setContainedItem(player, Side.SERVER, data, ItemStack.EMPTY);

        if (updateData) {
            ResearchManager.setPerkData(player, (AbstractPerk) this, prev, data);
        }
    }

    @SideOnly(Side.CLIENT)
    default public void addTooltipInfo(Collection<String> tooltip) {
        if (!(this instanceof AbstractPerk)) {
            return;
        }
        PlayerProgress prog = ResearchManager.getProgress(Minecraft.getMinecraft().thePlayer, Side.CLIENT);
        if (!prog.isValid()) {
            return;
        }
        ItemStack contained = getContainedItem(Minecraft.getMinecraft().thePlayer, Side.CLIENT);
        if (contained.isEmpty()) {
            tooltip.add(TextFormatting.GRAY + I18n.format("perk.info.gem.empty"));
            if (prog.hasPerkEffect((AbstractPerk) this)) {
                tooltip.add(TextFormatting.GRAY + I18n.format("perk.info.gem.content.empty"));

                boolean has = !ItemUtils
                    .findItemsIndexedInPlayerInventory(
                        Minecraft.getMinecraft().thePlayer,
                        s -> !s.isEmpty() && s.getItem() instanceof ItemPerkGem
                            && !ItemPerkGem.getModifiers(s)
                            .isEmpty())
                    .isEmpty();
                if (!has) {
                    tooltip.add(TextFormatting.RED + I18n.format("perk.info.gem.content.empty.none"));
                }
            }
        } else {
            tooltip.add(
                TextFormatting.GRAY + I18n.format(
                    "perk.info.gem.content.item",
                    contained.getRarity().rarityColor + contained.getDisplayName()));
            if (prog.hasPerkEffect((AbstractPerk) this)) {
                tooltip.add(TextFormatting.GRAY + I18n.format("perk.info.gem.remove"));
            }
        }
    }

}
