/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;

import crafttweaker.CraftTweakerAPI;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.network.SerializeableRecipe;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.*;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.AltarRecipe;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.GameStageTweaks;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.GrindstoneRecipe;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.InfusionRecipe;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.LightTransmutations;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.LiquidInteraction;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.PerkTree;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.Utils;
import shordinger.astralsorcery.common.integrations.mods.crafttweaker.tweaks.WellRecipe;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationCrafttweaker
 * Created by HellFirePvP
 * Date: 16.07.2017 / 17:38
 */
public class ModIntegrationCrafttweaker {

    public static ModIntegrationCrafttweaker instance = new ModIntegrationCrafttweaker();
    public static List<SerializeableRecipe> recipeModifications = new LinkedList<>();

    private ModIntegrationCrafttweaker() {
    }

    public void load() {
        CraftTweakerAPI.registerClass(InfusionRecipe.class);
        CraftTweakerAPI.registerClass(GrindstoneRecipe.class);
        CraftTweakerAPI.registerClass(LightTransmutations.class);
        CraftTweakerAPI.registerClass(AltarRecipe.class);
        CraftTweakerAPI.registerClass(WellRecipe.class);
        CraftTweakerAPI.registerClass(LiquidInteraction.class);

        CraftTweakerAPI.registerClass(PerkTree.class);

        CraftTweakerAPI.registerClass(Utils.class);
        CraftTweakerAPI.registerClass(GameStageTweaks.class);

        // For the perk removal / disabling events
        MinecraftForge.EVENT_BUS.register(new PerkTree());
    }

    public void pushChanges() {
        AstralSorcery.log
            .info("Got " + recipeModifications.size() + " recipe modifications from CraftTweaker. - Applying...");
        for (SerializeableRecipe recipe : recipeModifications) {
            try {
                recipe.applyRecipe();
            } catch (Exception exc) {
                AstralSorcery.log.error(
                    "Couldn't apply RecipeModification for type " + recipe.getType()
                        .name()
                        .toLowerCase());
                exc.printStackTrace();
            }
        }
        AstralSorcery.log.info("Recipe changes applied.");
    }

}
