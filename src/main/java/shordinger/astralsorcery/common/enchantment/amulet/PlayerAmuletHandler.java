/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment.amulet;

import java.util.EnumSet;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.enchantment.EnchantmentPlayerWornTick;
import shordinger.astralsorcery.common.event.DynamicEnchantmentEvent;
import shordinger.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import shordinger.astralsorcery.common.registry.RegistryEnchantments;
import shordinger.astralsorcery.common.util.data.Tuple;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAmuletHandler
 * Created by HellFirePvP
 * Date: 14.04.2018 / 17:49
 */
public class PlayerAmuletHandler implements ITickHandler {

    public static final PlayerAmuletHandler INSTANCE = new PlayerAmuletHandler();

    private PlayerAmuletHandler() {
    }

    @SubscribeEvent
    public void attachAmuletItemCapability(AttachCapabilitiesEvent<ItemStack> itemCapEvent) {
        if (!EnchantmentUpgradeHelper.isItemBlacklisted(itemCapEvent.getObject())) {
            itemCapEvent
                .addCapability(AmuletHolderCapability.CAP_AMULETHOLDER_NAME, new AmuletHolderCapability.Provider());
        }
    }

    @SubscribeEvent
    public void onAmuletEnchantApply(DynamicEnchantmentEvent.Add event) {
        if (EnchantmentUpgradeHelper.isItemBlacklisted(event.getEnchantedItemStack())) return;
        Tuple<ItemStack, EntityPlayer> linkedAmulet = EnchantmentUpgradeHelper
            .getWornAmulet(event.getEnchantedItemStack());
        if (linkedAmulet == null || linkedAmulet.key.isEmpty() || linkedAmulet.value == null) return;

        event.getEnchantmentsToApply()
            .addAll(ItemEnchantmentAmulet.getAmuletEnchantments(linkedAmulet.key));
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EntityPlayer player = (EntityPlayer) context[0];
        applyAmuletTags(player);
        clearAmuletTags(player);

        boolean client = player.getEntityWorld().isRemote;
        for (EnchantmentPlayerWornTick e : RegistryEnchantments.wearableTickEnchantments) {
            int max = EnchantmentHelper.getMaxEnchantmentLevel(e, player);
            if (max > 0) {
                e.onWornTick(client, player, max);
            }
        }
    }

    private void applyAmuletTags(EntityPlayer player) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && !EnchantmentUpgradeHelper.isItemBlacklisted(stack)) {
                EnchantmentUpgradeHelper.applyAmuletOwner(player.getItemStackFromSlot(slot), player);
            }
        }
    }

    private void clearAmuletTags(EntityPlayer player) {
        EnchantmentUpgradeHelper.removeAmuletTagsAndCleanup(player, true);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "PlayerAmuletHandler";
    }

}
