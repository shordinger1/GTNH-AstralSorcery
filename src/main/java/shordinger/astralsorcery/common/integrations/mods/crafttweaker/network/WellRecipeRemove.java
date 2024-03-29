/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeRemove
 * Created by HellFirePvP
 * Date: 28.02.2017 / 00:09
 */
public class WellRecipeRemove implements SerializeableRecipe {

    private ItemStack matchIn;
    private Fluid fluidOut;

    WellRecipeRemove() {}

    public WellRecipeRemove(ItemStack matchIn, @Nullable Fluid fluidOut) {
        this.matchIn = matchIn;
        this.fluidOut = fluidOut;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.WELL_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.matchIn = ByteBufUtils.readItemStack(buf);
        if (buf.readBoolean()) {
            this.fluidOut = FluidRegistry.getFluid(ByteBufUtils.readString(buf));
        } else {
            this.fluidOut = null;
        }
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.matchIn);
        boolean defined = this.fluidOut != null;
        buf.writeBoolean(defined);
        if (defined) {
            ByteBufUtils.writeString(buf, this.fluidOut.getName());
        }
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.removeMTLiquefaction(matchIn, fluidOut);
    }

}
