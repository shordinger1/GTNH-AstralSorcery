/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.util.ByteBufUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInteractionRemove
 * Created by HellFirePvP
 * Date: 20.04.2018 / 09:56
 */
public class LiquidInteractionRemove implements SerializeableRecipe {

    private FluidStack comp1, comp2;
    private ItemStack output;

    LiquidInteractionRemove() {
    }

    public LiquidInteractionRemove(FluidStack comp1, FluidStack comp2, ItemStack output) {
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.output = output;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.LIQINTERACTION_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.comp1 = ByteBufUtils.readFluidStack(buf);
        this.comp2 = ByteBufUtils.readFluidStack(buf);
        this.output = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeFluidStack(buf, this.comp1);
        ByteBufUtils.writeFluidStack(buf, this.comp2);
        ByteBufUtils.writeItemStack(buf, this.output);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.removeLiquidInteraction(comp1.getFluid(), comp2.getFluid(), output);
    }
}
