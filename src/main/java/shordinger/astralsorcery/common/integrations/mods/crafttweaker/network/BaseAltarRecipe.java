/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.DiscoveryRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import shordinger.astralsorcery.common.crafting.helper.AccessibleRecipeAdapater;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipe;
import shordinger.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.migration.MathHelper;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaseAltarRecipe
 * Created by HellFirePvP
 * Date: 27.02.2017 / 15:17
 */
public abstract class BaseAltarRecipe implements SerializeableRecipe {

    protected String name;
    protected ItemHandle[] inputs;
    protected ItemStack output;
    protected int starlightRequired, craftingTickTime;

    public BaseAltarRecipe(String name, ItemHandle[] inputs, ItemStack output, int starlightRequired,
                           int craftingTickTime) {
        this.name = name;
        this.inputs = inputs;
        this.output = output;
        this.starlightRequired = starlightRequired;
        this.craftingTickTime = craftingTickTime;
    }

    @Override
    public void read(ByteBuf buf) {
        this.name = ByteBufUtils.readString(buf);
        this.starlightRequired = buf.readInt();
        this.craftingTickTime = buf.readInt();
        this.output = ByteBufUtils.readItemStack(buf);
        int size = buf.readInt();
        this.inputs = new ItemHandle[size];
        for (int i = 0; i < size; i++) {
            boolean defined = buf.readBoolean();
            if (defined) {
                this.inputs[i] = ItemHandle.deserialize(buf);
            }
        }
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeString(buf, this.name);
        buf.writeInt(this.starlightRequired);
        buf.writeInt(this.craftingTickTime);
        ByteBufUtils.writeItemStack(buf, this.output);
        buf.writeInt(this.inputs.length);
        for (ItemHandle handle : this.inputs) {
            buf.writeBoolean(handle != null);
            if (handle != null) {
                handle.serialize(buf);
            }
        }
    }

    public List<Integer> computeFluidConsumptionSlots(ItemHandle[] inputs) {
        List<Integer> fluidInputs = Lists.newLinkedList();
        for (int i = 0; i < inputs.length; i++) {
            ItemHandle handle = inputs[i];
            if (handle == null) continue;
            if (handle.handleType == ItemHandle.Type.FLUID) {
                fluidInputs.add(i);
            }
        }
        return fluidInputs;
    }

    protected AbstractAltarRecipe buildRecipeUnsafe(TileAltar.AltarLevel altarLevel, int starlightConsumption,
                                                    int craftingTickTime, ItemStack out, ItemHandle[] inputs) {
        starlightConsumption = MathHelper.clamp(starlightConsumption, 1, altarLevel.getStarlightMaxStorage());
        final int sConsumption = starlightConsumption;
        List<Integer> fluidStacks = computeFluidConsumptionSlots(inputs);
        switch (altarLevel) {
            case DISCOVERY:
                return new DiscoveryRecipe(buildNativeRecipe(inputs, out)) {

                    @Override
                    public int getPassiveStarlightRequired() {
                        return sConsumption;
                    }

                    @Override
                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }
                };
            case ATTUNEMENT:
                AttunementRecipe rec = new AttunementRecipe(buildNativeRecipe(inputs, out)) {

                    @Override
                    public int getPassiveStarlightRequired() {
                        return sConsumption;
                    }

                    @Override
                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }
                };
                for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) rec.setAttItem(inputs[al.getSlotId()], al);
                }
                return rec;
            case CONSTELLATION_CRAFT:
                ConstellationRecipe cRec = new ConstellationRecipe(buildNativeRecipe(inputs, out)) {

                    @Override
                    public int getPassiveStarlightRequired() {
                        return sConsumption;
                    }

                    @Override
                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, ConstellationAtlarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }
                };
                for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) cRec.setAttItem(inputs[al.getSlotId()], al);
                }
                for (ConstellationRecipe.ConstellationAtlarSlot al : ConstellationRecipe.ConstellationAtlarSlot
                    .values()) {
                    if (inputs[al.getSlotId()] != null) cRec.setCstItem(inputs[al.getSlotId()], al);
                }
                return cRec;
            case TRAIT_CRAFT:
                TraitRecipe rRec = new TraitRecipe(buildNativeRecipe(inputs, out)) {

                    @Override
                    public int getPassiveStarlightRequired() {
                        return sConsumption;
                    }

                    @Override
                    public int craftingTickTime() {
                        return craftingTickTime;
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, ShapedRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotID());
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, AttunementAltarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, ConstellationAtlarSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }

                    @Override
                    public boolean mayDecrement(TileAltar ta, TraitRecipeSlot slot) {
                        return !fluidStacks.contains(slot.getSlotId());
                    }
                };
                for (AttunementRecipe.AttunementAltarSlot al : AttunementRecipe.AttunementAltarSlot.values()) {
                    if (inputs[al.getSlotId()] != null) rRec.setAttItem(inputs[al.getSlotId()], al);
                }
                for (ConstellationRecipe.ConstellationAtlarSlot al : ConstellationRecipe.ConstellationAtlarSlot
                    .values()) {
                    if (inputs[al.getSlotId()] != null) rRec.setCstItem(inputs[al.getSlotId()], al);
                }
                for (TraitRecipe.TraitRecipeSlot al : TraitRecipe.TraitRecipeSlot.values()) {
                    if (inputs[al.getSlotId()] != null) rRec.setInnerTraitItem(inputs[al.getSlotId()], al);
                }
                for (int i = 25; i < inputs.length; i++) {
                    if (inputs[i] != null) {
                        rRec.addOuterTraitItem(inputs[i]);
                    }
                }
                return rRec;
            default:
                break;
        }
        return null;
    }

    private AccessibleRecipeAdapater buildNativeRecipe(ItemHandle[] inputs, ItemStack out) {
        ShapedRecipe.Builder builder = ShapedRecipe.Builder.newShapedRecipe(this.name, out);
        for (int i = 0; i < 9; i++) {
            ItemHandle itemHandle = inputs[i];
            if (itemHandle == null) continue;
            ShapedRecipeSlot srs = ShapedRecipeSlot.values()[i];
            builder.addPart(inputs[i], srs);
        }
        return builder.unregisteredAccessibleShapedRecipe();
    }

}
