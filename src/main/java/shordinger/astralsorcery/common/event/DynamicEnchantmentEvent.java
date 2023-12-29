/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicEnchantmentEvent
 * Created by HellFirePvP
 * Date: 10.08.2018 / 20:25
 */
public class DynamicEnchantmentEvent {

    // The event to ADD new dynamic enchantments
    public static class Add extends Event {

        private final List<DynamicEnchantment> enchantmentsToApply = new LinkedList<>();
        private final ItemStack itemStack;
        private final EntityPlayer resolvedPlayer; // If the player could be resolved through this event, pass it down
        // to modify

        public Add(ItemStack itemStack, @Nullable EntityPlayer player) {
            this.itemStack = itemStack;
            this.resolvedPlayer = player;
        }

        public ItemStack getEnchantedItemStack() {
            return itemStack;
        }

        @Nullable
        public EntityPlayer getResolvedPlayer() {
            return resolvedPlayer;
        }

        public List<DynamicEnchantment> getEnchantmentsToApply() {
            return enchantmentsToApply;
        }
    }

    // The event to MODIFY or REACT to previously defined/added dynamic enchantments + enchantments
    public static class Modify extends Event {

        private final List<DynamicEnchantment> enchantmentsToApply;
        private final ItemStack itemStack;
        private final EntityPlayer resolvedPlayer;

        public Modify(ItemStack itemStack, List<DynamicEnchantment> enchantmentsToApply,
                      @Nullable EntityPlayer resolvedPlayer) {
            this.itemStack = itemStack;
            this.enchantmentsToApply = enchantmentsToApply;
            this.resolvedPlayer = resolvedPlayer;
        }

        @Nullable
        public EntityPlayer getResolvedPlayer() {
            return resolvedPlayer;
        }

        public ItemStack getEnchantedItemStack() {
            return itemStack;
        }

        public List<DynamicEnchantment> getEnchantmentsToApply() {
            return enchantmentsToApply;
        }
    }
}
