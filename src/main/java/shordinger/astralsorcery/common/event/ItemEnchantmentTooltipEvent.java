/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import shordinger.astralsorcery.core.ASMCallHook;
import shordinger.astralsorcery.migration.ITooltipFlag;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEnchantmentTooltipEvent
 * Created by HellFirePvP
 * Date: 20.05.2018 / 16:53
 */
public class ItemEnchantmentTooltipEvent extends PlayerEvent {

    private final ITooltipFlag flags;
    @Nonnull
    private final ItemStack itemStack;
    private final List<String> toolTip;

    @ASMCallHook
    public ItemEnchantmentTooltipEvent(@Nonnull ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip,
                                       ITooltipFlag flags) {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.toolTip = toolTip;
        this.flags = flags;
    }

    public ITooltipFlag getFlags() {
        return flags;
    }

    @Nonnull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<String> getToolTip() {
        return toolTip;
    }

}
