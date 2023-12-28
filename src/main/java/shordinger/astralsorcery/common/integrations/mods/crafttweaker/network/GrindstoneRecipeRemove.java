/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
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
 * Class: GrindstoneRecipeRemove
 * Created by HellFirePvP
 * Date: 30.11.2017 / 16:57
 */
public class GrindstoneRecipeRemove implements SerializeableRecipe {

    private ItemStack out;

    GrindstoneRecipeRemove() {
    }

    public GrindstoneRecipeRemove(ItemStack out) {
        this.out = out;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.GRINDSTONE_REMOVE;
    }

    @Override
    public void read(ByteBuf buf) {
        this.out = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, this.out);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.removeGrindstoneRecipe(this.out);
    }

}
