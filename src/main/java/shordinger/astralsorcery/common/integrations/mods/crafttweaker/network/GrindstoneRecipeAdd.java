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
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.util.ByteBufUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GrindstoneRecipeAdd
 * Created by HellFirePvP
 * Date: 30.11.2017 / 16:57
 */
public class GrindstoneRecipeAdd implements SerializeableRecipe {

    private ItemHandle in;
    private ItemStack out;
    private float doubleChance = 0F;

    GrindstoneRecipeAdd() {
    }

    public GrindstoneRecipeAdd(ItemHandle in, ItemStack out) {
        this(in, out, 0F);
    }

    public GrindstoneRecipeAdd(ItemHandle in, ItemStack out, float doubleChance) {
        this.in = in;
        this.out = out;
        this.doubleChance = doubleChance;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.GRINDSTONE_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        this.in = ItemHandle.deserialize(buf);
        this.out = ByteBufUtils.readItemStack(buf);
        this.doubleChance = buf.readFloat();
    }

    @Override
    public void write(ByteBuf buf) {
        this.in.serialize(buf);
        ByteBufUtils.writeItemStack(buf, this.out);
        buf.writeFloat(this.doubleChance);
    }

    @Override
    public void applyRecipe() {
        CraftingAccessManager.addGrindstoneRecipe(this.in, this.out, 12, this.doubleChance);
    }

}
