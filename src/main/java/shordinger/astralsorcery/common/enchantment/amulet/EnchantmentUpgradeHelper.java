/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.enchantment.amulet;

import baubles.api.BaubleType;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import shordinger.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import shordinger.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import shordinger.astralsorcery.common.event.DynamicEnchantmentEvent;
import shordinger.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import shordinger.astralsorcery.common.util.BaublesHelper;
import shordinger.astralsorcery.common.util.ItemComparator;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.core.ASMCallHook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentUpgradeHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 11:27
 */
public class EnchantmentUpgradeHelper {

    public static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item) {
        if (isItemBlacklisted(item)) return current;
        return getNewEnchantmentLevel(current, currentEnchantmentId, item, null);
    }

    @ASMCallHook
    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item) {
        if (isItemBlacklisted(item)) return current;
        return getNewEnchantmentLevel(current, enchantment, item, null);
    }

    private static int getNewEnchantmentLevel(int current, int currentEnchantmentId, ItemStack item,
                                              @Nullable List<DynamicEnchantment> context) {
        if (isItemBlacklisted(item)) return current;
        Enchantment ench = Enchantment.getEnchantmentByID(currentEnchantmentId);
        if (ench != null) {
            return getNewEnchantmentLevel(current, ench, item, context);
        }
        return current;
    }

    private static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack item,
                                              @Nullable List<DynamicEnchantment> context) {
        if (isItemBlacklisted(item)) return current;

        if (item.isEmpty() || !AmuletEnchantmentRegistry.canBeInfluenced(enchantment)) {
            return current;
        }

        List<DynamicEnchantment> modifiers = context != null ? context : fireEnchantmentGatheringEvent(item);
        for (DynamicEnchantment mod : modifiers) {
            Enchantment target = mod.getEnchantment();
            switch (mod.getType()) {
                case ADD_TO_SPECIFIC -> {
                    if (enchantment.equals(target)) {
                        current += mod.getLevelAddition();
                    }
                }
                case ADD_TO_EXISTING_SPECIFIC -> {
                    if (enchantment.equals(target) && current > 0) {
                        current += mod.getLevelAddition();
                    }
                }
                case ADD_TO_EXISTING_ALL -> {
                    if (current > 0) {
                        current += mod.getLevelAddition();
                    }
                }
                default -> {
                }
            }
        }
        return current;
    }

    @ASMCallHook
    public static NBTTagList modifyEnchantmentTags(@Nonnull NBTTagList existingEnchantments, ItemStack stack) {
        if (isItemBlacklisted(stack)) return existingEnchantments;

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if (context.isEmpty()) return existingEnchantments;

        NBTTagList returnNew = new NBTTagList();
        List<Enchantment> enchantments = new ArrayList<>(existingEnchantments.tagCount());
        for (int i = 0; i < existingEnchantments.tagCount(); i++) {
            NBTTagCompound cmp = existingEnchantments.getCompoundTagAt(i);
            int enchId = cmp.getShort("id");
            int lvl = cmp.getShort("lvl");
            int newLvl = getNewEnchantmentLevel(lvl, enchId, stack, context);

            NBTTagCompound newEnchTag = new NBTTagCompound();
            newEnchTag.setShort("id", (short) enchId);
            newEnchTag.setShort("lvl", (short) newLvl);
            returnNew.appendTag(newEnchTag);
            Enchantment e = Enchantment.getEnchantmentByID(enchId);
            if (e != null) { // If that is actually null, something went terribly wrong.
                enchantments.add(e);
            }
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantment.Type.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (!AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnumEnchantmentType type = null;
                if (ench != null) {
                    type = ench.type;
                }
                if (type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if (!enchantments.contains(ench)) { // Means we didn't add the levels on the other iteration
                    NBTTagCompound newEnchTag = new NBTTagCompound();
                    newEnchTag.setShort("id", (short) Enchantment.getEnchantmentID(ench));
                    newEnchTag.setShort("lvl", (short) getNewEnchantmentLevel(0, ench, stack, context));
                    returnNew.appendTag(newEnchTag);
                }
            }
        }
        return returnNew;
    }

    @ASMCallHook
    public static Map<Enchantment, Integer> applyNewEnchantmentLevels(Map<Enchantment, Integer> enchantmentLevelMap,
                                                                      ItemStack stack) {
        if (isItemBlacklisted(stack)) return enchantmentLevelMap;

        List<DynamicEnchantment> context = fireEnchantmentGatheringEvent(stack);
        if (context.isEmpty()) return enchantmentLevelMap;

        Map<Enchantment, Integer> copyRet = Maps.newLinkedHashMap();
        for (Map.Entry<Enchantment, Integer> enchant : enchantmentLevelMap.entrySet()) {
            copyRet.put(enchant.getKey(), getNewEnchantmentLevel(enchant.getValue(), enchant.getKey(), stack, context));
        }

        for (DynamicEnchantment mod : context) {
            if (mod.getType() == DynamicEnchantment.Type.ADD_TO_SPECIFIC) {
                Enchantment ench = mod.getEnchantment();
                if (!AmuletEnchantmentRegistry.canBeInfluenced(ench)) {
                    continue;
                }
                EnumEnchantmentType type = null;
                if (ench != null) {
                    type = ench.type;
                }
                if (type != null && !type.canEnchantItem(stack.getItem())) {
                    continue;
                }
                if (!enchantmentLevelMap.containsKey(ench)) { // Means we didn't add the levels on the other iteration
                    copyRet.put(ench, getNewEnchantmentLevel(0, ench, stack, context));
                }
            }
        }
        return copyRet;
    }

    public static boolean isItemBlacklisted(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                return true; // We're not gonna apply enchantments to items used for querying matches
            }

            if (stack.getMaxStackSize() > 1) {
                return true; // Only swords & armor and stuff that isn't stackable
            }
            if (stack.getItem() instanceof ItemPotion || stack.getItem() instanceof ItemEnchantedBook) {
                return true; // Not gonna apply enchantments to potions or books
            }

            ResourceLocation rl = stack.getItem()
                .getRegistryName();
            if (rl == null) return true; // Yea... no questions asked i guess.

            // Exploit with DE's item-GUI being able to draw item's enchantments while having it equipped
            // causes infinite feedback loop stacking enchantments higher and higher.
            return rl.getResourceDomain()
                .equalsIgnoreCase("draconicevolution");
        }
        return true;
    }

    // ---------------------------------------------------
    // Data organization
    // ---------------------------------------------------

    // This is more or less just a map to say whatever we add upon.
    private static List<DynamicEnchantment> fireEnchantmentGatheringEvent(ItemStack tool) {
        DynamicEnchantmentEvent.Add addEvent = new DynamicEnchantmentEvent.Add(tool, getPlayerHavingTool(tool));
        MinecraftForge.EVENT_BUS.post(addEvent);
        DynamicEnchantmentEvent.Modify modifyEvent = new DynamicEnchantmentEvent.Modify(
            tool,
            addEvent.getEnchantmentsToApply(),
            addEvent.getResolvedPlayer());
        MinecraftForge.EVENT_BUS.post(modifyEvent);
        return modifyEvent.getEnchantmentsToApply();
    }

    public static void removeAmuletTagsAndCleanup(EntityPlayer player, boolean keepEquipped) {
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < inv.mainInventory.length; i++) {
            if (i == inv.currentItem && keepEquipped) continue;
            removeAmuletOwner(inv.mainInventory[i]);
        }
        removeAmuletOwner(inv.getItemStack());
        if (!keepEquipped) {
            for (int i = 0; i < inv.armorInventory.length; i++) {
                removeAmuletOwner(inv.armorInventory[i]);
            }
            // for (int i = 0; i < inv.offHandInventory.size(); i++) {
            // removeAmuletOwner(inv.offHandInventory.get(i));
            // }
        }
    }

    @Nullable
    private static UUID getWornPlayerUUID(ItemStack anyTool) {
        if (!anyTool.isEmpty() && anyTool.hasCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null)) {
            AmuletHolderCapability cap = anyTool.getCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null);
            if (cap != null) {
                return cap.getHolderUUID();
            }
        }
        return null;
    }

    public static void applyAmuletOwner(ItemStack tool, EntityPlayer wearer) {
        if (tool.isEmpty() || !tool.hasCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null)) return;
        AmuletHolderCapability cap = tool.getCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null);
        if (cap == null) return;
        cap.setHolderUUID(wearer.getUniqueID());
    }

    private static void removeAmuletOwner(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null)) {
            return;
        }
        AmuletHolderCapability cap = stack.getCapability(AmuletHolderCapability.CAPABILITY_AMULET_HOLDER, null);
        if (cap == null) return;
        cap.setHolderUUID(null);
    }

    @Nullable
    static EntityPlayer getPlayerHavingTool(ItemStack anyTool) {
        UUID plUUID = getWornPlayerUUID(anyTool);
        if (plUUID == null) return null;
        EntityPlayer player;
        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.CLIENT) {
            player = resolvePlayerClient(plUUID);
        } else {
            MinecraftServer server = FMLCommonHandler.instance()
                .getMinecraftServerInstance();
            if (server == null) return null;
            player = server.getPlayerList()
                .getPlayerByUUID(plUUID);
        }
        if (player == null) return null;

        if (!ItemUtils.findItemsIndexedInPlayerInventory(
                player,
                stack -> stack.getItem()
                    .getRegistryName()
                    .toString()
                    .equals("tombstone:book_of_disenchantment"))
            .isEmpty()) {
            return null;
        }

        // Check if the player actually wears/carries the tool
        boolean foundTool = false;
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (ItemComparator.compare(stack, anyTool, ItemComparator.Clause.Sets.ITEMSTACK_STRICT)) {
                foundTool = true;
                break;
            }
        }
        if (!foundTool) return null;

        return player;
    }

    @Nullable
    static Tuple<ItemStack, EntityPlayer> getWornAmulet(ItemStack anyTool) {
        EntityPlayer player = getPlayerHavingTool(anyTool);
        if (player == null) return null;

        // Check if the player wears an amulet and return that one then..
        if (BaublesHelper.doesPlayerWearBauble(
            player,
            BaubleType.AMULET,
            (stack) -> !stack.isEmpty() && stack.getItem() instanceof ItemEnchantmentAmulet)) {
            ItemStack stack = BaublesHelper.getFirstWornBaublesForType(player, BaubleType.AMULET);
            return new Tuple<>(stack, player);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static EntityPlayer resolvePlayerClient(UUID plUUID) {
        World w = FMLClientHandler.instance()
            .getWorldClient();
        if (w == null) return null;
        return w.getPlayerEntityByUUID(plUUID);
    }

}
