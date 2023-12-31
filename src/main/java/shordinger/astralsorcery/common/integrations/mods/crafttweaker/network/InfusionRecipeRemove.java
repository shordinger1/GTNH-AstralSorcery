/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import net.minecraft.item.ItemStack;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.util.ByteBufUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusionRecipeRemove
 * Created by HellFirePvP
 * Date: 27.02.2017 / 02:33
 */
public class InfusionRecipeRemove implements SerializeableRecipe {

    private ItemStack removalOut;

    InfusionRecipeRemove() {
    }

    public InfusionRecipeRemove(ItemStack out) {
        this.removalOut = out;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.INFUSION_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.removalOut = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.removalOut);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.tryRemoveInfusionByOutput(this.removalOut);
    }

}
