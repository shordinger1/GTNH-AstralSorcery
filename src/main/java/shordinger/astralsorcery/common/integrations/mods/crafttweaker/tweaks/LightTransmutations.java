/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks;

import net.minecraft.item.ItemStack;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IItemStack;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.integrations.ModIntegrationCrafttweaker;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.BaseTweaker;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.network.LightTransmutationAdd;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.network.LightTransmutationRemove;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.IBlockState;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LightTransmutations
 * Created by HellFirePvP
 * Date: 27.02.2017 / 11:32
 */
@ZenClass("mods.astralsorcery.LightTransmutation")
public class LightTransmutations extends BaseTweaker {

    protected static final String name = "AstralSorcery Starlight Transmutation";

    @ZenMethod
    public static void addTransmutation(IItemStack stackIn, IItemStack stackOut, double cost,
                                        String requiredConstellation) {
        ItemStack in = convertToItemStack(stackIn);
        ItemStack out = convertToItemStack(stackOut);
        if (in.isEmpty() || out.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe due to invalid input/output.");
            return;
        }

        IBlockState state = ItemUtils.createBlockState(in);
        if (state == null) {
            CraftTweakerAPI
                .logError("[" + name + "] Skipping recipe - Can't create a valid BlockState from given Input");
            return;
        }
        state = ItemUtils.createBlockState(out);
        if (state == null) {
            CraftTweakerAPI
                .logError("[" + name + "] Skipping recipe - Can't create a valid BlockState from given Output");
            return;
        }

        IWeakConstellation req = null;
        if (requiredConstellation != null && !requiredConstellation.isEmpty()) {
            IConstellation cst = ConstellationRegistry.getConstellationByName(requiredConstellation);
            if (cst != null && cst instanceof IWeakConstellation) {
                req = (IWeakConstellation) cst;
            } else {
                CraftTweakerAPI.logError(
                    "[" + name
                        + "] Skipping recipe - Unknown or Non-Bright/Non-Dim constellation: "
                        + requiredConstellation);
                return;
            }
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new LightTransmutationAdd(in, out, cost, req));
    }

    @ZenMethod
    public static void addTransmutation(IItemStack stackIn, IItemStack stackOut, double cost) {
        addTransmutation(stackIn, stackOut, cost, null);
    }

    @ZenMethod
    public static void removeTransmutation(IItemStack stackToRemove, boolean matchMeta) {
        ItemStack removeMatch = convertToItemStack(stackToRemove);
        if (removeMatch.isEmpty()) {
            CraftTweakerAPI.logError("[" + name + "] Skipping recipe-removal due to invalid output.");
            return;
        }

        ModIntegrationCrafttweaker.recipeModifications.add(new LightTransmutationRemove(removeMatch, matchMeta));
    }

}
