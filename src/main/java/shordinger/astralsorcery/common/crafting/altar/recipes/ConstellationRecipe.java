/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 22:22
 */
public class ConstellationRecipe extends AttunementRecipe {

    private static final Vector3[] offsetPillars = new Vector3[]{new Vector3(4, 3, 4), new Vector3(-4, 3, 4),
        new Vector3(4, 3, -4), new Vector3(-4, 3, -4)};

    private final Map<ConstellationAtlarSlot, ItemHandle> matchStacks = new HashMap<>();

    protected ConstellationRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public ConstellationRecipe(AccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.CONSTELLATION_CRAFT, recipe);
        setPassiveStarlightRequirement(3200);
    }

    public ConstellationRecipe setCstItem(Item i, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemStack(i), slots);
    }

    public ConstellationRecipe setCstItem(Block b, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemStack(b), slots);
    }

    public ConstellationRecipe setCstItem(ItemStack stack, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(stack), slots);
    }

    public ConstellationRecipe setCstItem(String oreDict, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(oreDict), slots);
    }

    public ConstellationRecipe setCstItem(FluidStack fluid, ConstellationAtlarSlot... slots) {
        return setCstItem(new ItemHandle(fluid), slots);
    }

    public ConstellationRecipe setCstItem(Fluid fluid, int mbAmount, ConstellationAtlarSlot... slots) {
        return setCstItem(new FluidStack(fluid, mbAmount), slots);
    }

    public ConstellationRecipe setCstItem(Fluid fluid, ConstellationAtlarSlot... slots) {
        return setCstItem(fluid, 1000, slots);
    }

    public ConstellationRecipe setCstItem(ItemHandle handle, ConstellationAtlarSlot... slots) {
        for (ConstellationAtlarSlot slot : slots) {
            matchStacks.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getCstItems(ConstellationAtlarSlot slot) {
        ItemHandle handle = matchStacks.get(slot);
        if (handle != null) {
            return handle.getApplicableItems();
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getCstItemHandle(ConstellationAtlarSlot slot) {
        return matchStacks.get(slot);
    }

    @Override
    public int craftingTickTime() {
        return 500;
    }

    @Override
    public void handleInputConsumption(TileAltar ta, ActiveCraftingTask craftingTask, ItemStackHandler inventory) {
        super.handleInputConsumption(ta, craftingTask, inventory);

        for (ConstellationRecipe.ConstellationAtlarSlot slot : ConstellationRecipe.ConstellationAtlarSlot.values()) {
            int slotId = slot.getSlotId();
            if (mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, slotId);
            } else {
                handleItemConsumption(ta, slot);
            }
        }
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler,
                           boolean ignoreStarlightRequirement) {
        for (ConstellationAtlarSlot slot : ConstellationAtlarSlot.values()) {
            ItemHandle expected = matchStacks.get(slot);
            if (expected != null) {
                ItemStack altarItem = invHandler.getStackInSlot(slot.slotId);
                if (!expected.matchCrafting(altarItem)) {
                    return false;
                }
            } else {
                if (!invHandler.getStackInSlot(slot.slotId)
                    .isEmpty()) return false;
            }
        }
        return super.matches(altar, invHandler, ignoreStarlightRequirement);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 altarVec = new Vector3(altar);
            Vector3 thisAltar = altarVec.clone()
                .add(0.5, 0.5, 0.5);
            for (int i = 0; i < 4; i++) {
                Vector3 dir = offsetPillars[rand.nextInt(offsetPillars.length)].clone();
                dir.multiply(rand.nextFloat())
                    .add(thisAltar.clone());

                EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(dir.getX(), dir.getY(), dir.getZ());
                particle.setColor(MiscUtils.calcRandomConstellationColor(rand.nextFloat()))
                    .scale(0.2F + (0.2F * rand.nextFloat()))
                    .gravity(0.004);
            }
        }

    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.CONSTELLATION;
    }

    public static enum ConstellationAtlarSlot {

        UP_UP_LEFT(13),
        UP_UP_RIGHT(14),
        UP_LEFT_LEFT(15),
        UP_RIGHT_RIGHT(16),

        DOWN_LEFT_LEFT(17),
        DOWN_RIGHT_RIGHT(18),
        DOWN_DOWN_LEFT(19),
        DOWN_DOWN_RIGHT(20);

        private final int slotId;

        ConstellationAtlarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }
    }

}
