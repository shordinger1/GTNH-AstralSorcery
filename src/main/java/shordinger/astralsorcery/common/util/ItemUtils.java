/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import static shordinger.astralsorcery.common.util.ItemComparator.Clause.*;

import java.util.*;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;
import com.gtnewhorizons.modularui.api.forge.IItemHandler;
import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;

import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.integrations.ModIntegrationBotania;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUtils
 * Created by HellFirePvP
 * Date: 31.07.2016 / 17:51
 */
public class ItemUtils {

    private static final Random rand = new Random();

    public static EntityItem dropItem(World world, double x, double y, double z, ItemStack stack) {
        if (world.isRemote) return null;
        EntityItem ei = new EntityItem(world, x, y, z, stack);
        ei.motionX = 0;
        ei.motionY = 0;
        ei.motionZ = 0;
        world.spawnEntityInWorld(ei);
        ei.delayBeforeCanPickup = 10;
        return ei;
    }

    public static EntityItem dropItemNaturally(World world, double x, double y, double z, ItemStack stack) {
        if (world.isRemote) return null;
        EntityItem ei = new EntityItem(world, x, y, z, stack);
        applyRandomDropOffset(ei);
        world.spawnEntityInWorld(ei);
        ei.delayBeforeCanPickup = 10;
        return ei;
    }

    private static void applyRandomDropOffset(EntityItem item) {
        item.motionX = rand.nextFloat() * 0.3F - 0.15D;
        item.motionY = rand.nextFloat() * 0.3F - 0.15D;
        item.motionZ = rand.nextFloat() * 0.3F - 0.15D;
    }

    public static ItemStack createBlockStack(IBlockState state) {
        Item i = Item.getItemFromBlock(state.getBlock());
        if (i == null) return null;
        int meta = state.getBlock()
            .damageDropped(state);
        return new ItemStack(i, 1, meta);
    }

    @Nullable
    public static IBlockState createBlockState(ItemStack stack) {
        Block b = Block.getBlockFromItem(stack.getItem());
        if (b == null) return null;
        try {
            return b.getStateFromMeta(stack.getMetadata());
        } catch (Exception exc) {
            return b.getDefaultState();
        }
    }

    public static Collection<ItemStack> scanInventoryFor(IItemHandler handler, Item i) {
        List<ItemStack> out = new LinkedList<>();
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            if (s.stackSize!=0 && s.getItem() == i) out.add(copyStackWithSize(s, s.stackSize));
        }
        return out;
    }

    public static Collection<ItemStack> scanInventoryForMatching(IItemHandler handler, ItemStack match,
                                                                 boolean strict) {
        return findItemsInInventory(handler, match, strict);
    }

    public static Collection<ItemStack> findItemsInPlayerInventory(EntityPlayer player, ItemStack match,
                                                                   boolean strict) {
        return findItemsInInventory(
            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
            match,
            strict);
    }

    public static Collection<ItemStack> findItemsInInventory(IItemHandler handler, ItemStack match, boolean strict) {
        List<ItemStack> stacksOut = new LinkedList<>();
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            if (strict ? ItemComparator.compare(s, match, ITEM, META_STRICT, NBT_STRICT, CAPABILITIES_COMPATIBLE)
                : ItemComparator.compare(s, match, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                stacksOut.add(copyStackWithSize(s, s.stackSize));
            }
        }
        return stacksOut;
    }

    public static Map<Integer, ItemStack> findItemsIndexedInPlayerInventory(EntityPlayer player,
                                                                            Predicate<ItemStack> match) {
        return findItemsIndexedInInventory(
            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
            match);
    }

    public static Map<Integer, ItemStack> findItemsIndexedInInventory(IItemHandler handler, ItemStack match,
                                                                      boolean strict) {
        return findItemsIndexedInInventory(
            handler,
            (s) -> strict ? ItemComparator.compare(s, match, ITEM, META_STRICT, NBT_STRICT, CAPABILITIES_COMPATIBLE)
                : ItemComparator.compare(s, match, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT));
    }

    public static Map<Integer, ItemStack> findItemsIndexedInInventory(IItemHandler handler,
                                                                      Predicate<ItemStack> match) {
        Map<Integer, ItemStack> stacksOut = new HashMap<>();
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            if (match.test(s)) {
                stacksOut.put(j, copyStackWithSize(s, s.stackSize));
            }
        }
        return stacksOut;
    }

    public static boolean consumeFromPlayerInventory(EntityPlayer player, ItemStack requestingItemStack,
                                                     ItemStack toConsume, boolean simulate) {
        int consumed = 0;
        if (Mods.BOTANIA.isPresent()) {
            IBlockState consumeState = createBlockState(toConsume);
            if (consumeState != null) {
                Block b = consumeState.getBlock();
                int meta = b.damageDropped(consumeState);

                for (int i = 0; i < toConsume.stackSize; i++) {
                    ItemStack res = ModIntegrationBotania
                        .requestFromInventory(player, requestingItemStack, b, meta, !simulate);
                    if (!res.isEmpty()) {
                        consumed++;
                    }
                }
            }
        }
        ItemStack tryConsume = copyStackWithSize(toConsume, toConsume.stackSize - consumed);
        return tryConsume.isEmpty() || consumeFromInventory(
            (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
            tryConsume,
            simulate);
    }

    public static boolean tryConsumeFromInventory(IItemHandler handler, ItemStack toConsume, boolean simulate) {
        return handler instanceof IItemHandlerModifiable
            && consumeFromInventory((IItemHandlerModifiable) handler, toConsume, simulate);
    }

    public static boolean consumeFromInventory(IItemHandlerModifiable handler, ItemStack toConsume, boolean simulate) {
        Map<Integer, ItemStack> contents = findItemsIndexedInInventory(handler, toConsume, false);
        if (contents.isEmpty()) return false;

        int cAmt = toConsume.stackSize;
        for (int slot : contents.keySet()) {
            ItemStack inSlot = contents.get(slot);
            int toRemove = cAmt > inSlot.stackSize ? inSlot.stackSize : cAmt;
            cAmt -= toRemove;
            if (!simulate) {
                handler.setStackInSlot(slot, copyStackWithSize(inSlot, inSlot.stackSize - toRemove));
            }
            if (cAmt <= 0) {
                break;
            }
        }
        return cAmt <= 0;
    }

    public static void dropInventory(IItemHandler handle, World worldIn, BlockPos pos) {
        if (worldIn.isRemote) return;
        for (int i = 0; i < handle.getSlots(); i++) {
            ItemStack stack = handle.getStackInSlot(i);
            if (stack == null || stack.stackSize == 0) continue;
            dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        }
    }

    public static FluidActionResult drainFluidFromItem(ItemStack stack, Fluid fluid, int mbAmount, boolean doDrain) {
        return drainFluidFromItem(stack, new FluidStack(fluid, mbAmount), doDrain);
    }

    public static FluidActionResult drainFluidFromItem(ItemStack stack, FluidStack fluidStack, boolean doDrain) {
        return FluidUtil.tryEmptyContainer(stack, FluidHandlerVoid.INSTANCE, fluidStack.amount, null, doDrain);
    }

    /*
     * public static void decrStackInInventory(ItemStack[] stacks, int slot) {
     * if(slot < 0 || slot >= stacks.length) return;
     * ItemStack st = stacks[slot];
     * if(st == null) return;
     * st.stackSize--;
     * if(st.stackSize <= 0) {
     * stacks[slot] = null;
     * }
     * }
     */

    public static void decrStackInInventory(ItemStackHandler handler, int slot) {
        if (slot < 0 || slot >= handler.getSlots()) return;
        ItemStack st = handler.getStackInSlot(slot);
        if (st == null || st.stackSize == 0) return;
        st.stackSize--;
        if (st.stackSize <= 0) {
            handler.setStackInSlot(slot, null);
        }
    }

    public static boolean tryPlaceItemInInventory(@Nonnull ItemStack stack, IItemHandler handler) {
        return tryPlaceItemInInventory(stack, handler, 0, handler.getSlots());
    }

    public static boolean tryPlaceItemInInventory(@Nonnull ItemStack stack, IItemHandler handler, int start, int end) {
        ItemStack toAdd = stack.copy();
        if (!hasInventorySpace(toAdd, handler, start, end)) return false;
        int max = stack.getMaxStackSize();

        for (int i = start; i < end; i++) {
            ItemStack in = handler.getStackInSlot(i);
            if (in == null || in.stackSize == 0) {
                int added = Math.min(stack.stackSize, max);
                stack.stackSize -= added;
                handler.insertItem(i, copyStackWithSize(stack, added), false);
                return true;
            } else {
                if (ItemComparator.compare(stack, in, ITEM, META_STRICT, NBT_STRICT, CAPABILITIES_COMPATIBLE)) {

                    int space = max - in.stackSize;
                    int added = Math.min(stack.stackSize, space);
                    stack.stackSize -= added;
                    handler.getStackInSlot(i).stackSize += added;
                    if (stack.stackSize <= 0) return true;
                }
            }
        }
        return stack.stackSize == 0;
    }

    public static boolean hasInventorySpace(@Nonnull ItemStack stack, IItemHandler handler, int rangeMin,
                                            int rangeMax) {
        int size = stack.stackSize;
        int max = stack.getMaxStackSize();
        for (int i = rangeMin; i < rangeMax && size > 0; i++) {
            ItemStack in = handler.getStackInSlot(i);
            if (in == null || in.stackSize == 0) {
                size -= max;
            } else {
                if (ItemComparator.compare(stack, in, ITEM, META_STRICT, NBT_STRICT, CAPABILITIES_COMPATIBLE)) {

                    int space = max - in.stackSize;
                    size -= space;
                }
            }
        }
        return size <= 0;
    }

    public static ItemStack copyStackWithSize(@Nonnull ItemStack stack, int amount) {
        if (stack.stackSize == 0 || amount <= 0) return null;
        ItemStack s = stack.copy();
        s.stackSize = amount;
        return s;
    }

    public static boolean hasOreNamePart(ItemStack stack, String namePart) {
        namePart = namePart.toLowerCase();
        List<String> oreNames = getOreDictNames(stack);
        for (String s : oreNames) {
            if (s.contains(namePart)) return true;
        }
        return false;
    }

    public static boolean hasOreName(ItemStack stack, String name) {
        name = name.toLowerCase();
        return getOreDictNames(stack).contains(name);
    }

    public static NonNullList<ItemStack> getStacksOfOredict(String name) {
        return OreDictionary.getOres(name, false);
    }

    private static List<String> getOreDictNames(ItemStack stack) {
        List<String> out = Lists.newArrayList();
        for (int id : OreDictionary.getOreIDs(stack)) {
            out.add(
                OreDictionary.getOreName(id)
                    .toLowerCase());
        }
        return out;
    }

    private static class FluidHandlerVoid implements IFluidHandler {

        private static final FluidHandlerVoid INSTANCE = new FluidHandlerVoid();

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[0];
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return resource.amount;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }
    }

}
