/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.network;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.crafting.ItemHandle;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import shordinger.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import shordinger.astralsorcery.common.crafting.helper.CraftingAccessManager;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.util.ByteBufUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeTrait
 * Created by HellFirePvP
 * Date: 24.07.2017 / 19:46
 */
public class AltarRecipeTrait extends BaseAltarRecipe {

    @Nullable
    private IConstellation focusRequiredConstellation;

    AltarRecipeTrait() {
        super(null, null, null, 0, 0);
        this.focusRequiredConstellation = null;
    }

    public AltarRecipeTrait(String name, ItemHandle[] inputs, ItemStack output, int starlightRequired,
                            int craftingTickTime, @Nullable IConstellation focus) {
        super(name, inputs, output, starlightRequired, craftingTickTime);
        this.focusRequiredConstellation = focus;
    }

    @Override
    public CraftingType getType() {
        return CraftingType.ALTAR_T4_ADD;
    }

    @Override
    public void read(ByteBuf buf) {
        super.read(buf);
        if (buf.readBoolean()) {
            this.focusRequiredConstellation = ConstellationRegistry
                .getConstellationByName(ByteBufUtils.readString(buf));
        } else {
            this.focusRequiredConstellation = null;
        }
    }

    @Override
    public void write(ByteBuf buf) {
        super.write(buf);
        buf.writeBoolean(this.focusRequiredConstellation == null);
        if (this.focusRequiredConstellation != null) {
            ByteBufUtils.writeString(buf, this.focusRequiredConstellation.getUnlocalizedName());
        }
    }

    @Override
    public void applyRecipe() {
        AbstractAltarRecipe aar = buildRecipeUnsafe(
            TileAltar.AltarLevel.TRAIT_CRAFT,
            this.starlightRequired,
            this.craftingTickTime,
            this.output,
            this.inputs);
        if (aar instanceof TraitRecipe && focusRequiredConstellation != null) {
            ((TraitRecipe) aar).setRequiredConstellation(focusRequiredConstellation);
        }
        CraftingAccessManager.registerMTAltarRecipe(aar);
    }

}
