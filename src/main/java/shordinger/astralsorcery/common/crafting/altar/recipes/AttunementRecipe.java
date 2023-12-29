/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting.altar.recipes;

import java.util.Collections;
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

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipe;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttenuationRecipe
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:20
 */
public class AttunementRecipe extends DiscoveryRecipe {

    private final Map<AttunementAltarSlot, ItemHandle> additionalSlots = new HashMap<>();

    protected AttunementRecipe(TileAltar.AltarLevel neededLevel, AccessibleRecipe recipe) {
        super(neededLevel, recipe);
    }

    public AttunementRecipe(AccessibleRecipe recipe) {
        super(TileAltar.AltarLevel.ATTUNEMENT, recipe);
        setPassiveStarlightRequirement(1400);
    }

    public AttunementRecipe setAttItem(Block b, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemStack(b), slots);
    }

    public AttunementRecipe setAttItem(Item i, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemStack(i), slots);
    }

    public AttunementRecipe setAttItem(ItemStack stack, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(stack), slots);
    }

    public AttunementRecipe setAttItem(String oreDict, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(oreDict), slots);
    }

    public AttunementRecipe setAttItem(FluidStack fluid, AttunementAltarSlot... slots) {
        return this.setAttItem(new ItemHandle(fluid), slots);
    }

    public AttunementRecipe setAttItem(Fluid fluid, int mbAmount, AttunementAltarSlot... slots) {
        return setAttItem(new FluidStack(fluid, mbAmount), slots);
    }

    public AttunementRecipe setAttItem(Fluid fluid, AttunementAltarSlot... slots) {
        return setAttItem(fluid, 1000, slots);
    }

    public AttunementRecipe setAttItem(ItemHandle handle, AttunementAltarSlot... slots) {
        for (AttunementAltarSlot slot : slots) {
            additionalSlots.put(slot, handle);
        }
        return this;
    }

    @Nonnull
    public List<ItemStack> getAttItems(AttunementAltarSlot slot) {
        ItemHandle handle = additionalSlots.get(slot);
        if (handle != null) {
            return Collections.singletonList(handle.getApplicableItems());
        }
        return Lists.newArrayList();
    }

    @Nullable
    public ItemHandle getAttItemHandle(AttunementAltarSlot slot) {
        return additionalSlots.get(slot);
    }

    @Override
    public boolean matches(TileAltar altar, TileReceiverBaseInventory.ItemHandlerTile invHandler,
                           boolean ignoreStarlightRequirement) {
        for (AttunementAltarSlot slot : AttunementAltarSlot.values()) {
            ItemHandle expected = additionalSlots.get(slot);
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
    public int craftingTickTime() {
        return 300;
    }

    @Override
    public void handleInputConsumption(TileAltar ta, ActiveCraftingTask craftingTask, ItemStackHandler inventory) {
        super.handleInputConsumption(ta, craftingTask, inventory);

        for (AttunementRecipe.AttunementAltarSlot slot : AttunementRecipe.AttunementAltarSlot.values()) {
            int slotId = slot.getSlotId();
            if (mayDecrement(ta, slot)) {
                ItemUtils.decrStackInInventory(inventory, slotId);
            } else {
                handleItemConsumption(ta, slot);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, ActiveCraftingTask.CraftingState state, long tick, Random rand) {
        super.onCraftClientTick(altar, state, tick, rand);

        if (state == ActiveCraftingTask.CraftingState.ACTIVE) {
            Vector3 pos = new Vector3(altar).add(0.5, 0.5, 0.5);
            EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
            particle.motion(
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.05 * (rand.nextBoolean() ? 1 : -1));
            particle.scale(0.2F);
        }
    }

    @Nonnull
    @Override
    public ResearchProgression getRequiredProgression() {
        return ResearchProgression.ATTUNEMENT;
    }

    public static enum AttunementAltarSlot {

        UPPER_LEFT(9),
        UPPER_RIGHT(10),
        LOWER_LEFT(11),
        LOWER_RIGHT(12);

        private final int slotId;

        private AttunementAltarSlot(int slotId) {
            this.slotId = slotId;
        }

        public int getSlotId() {
            return slotId;
        }

    }

}
