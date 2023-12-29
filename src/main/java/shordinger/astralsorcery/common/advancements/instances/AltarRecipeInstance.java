/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements.instances;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeInstance
 * Created by HellFirePvP
 * Date: 27.10.2018 / 14:23
 */
public class AltarRecipeInstance extends AbstractCriterionInstance {

    private List<ResourceLocation> recipeNames = Lists.newArrayList();

    private AltarRecipeInstance(ResourceLocation id) {
        super(id);
    }

    public static AltarRecipeInstance deserialize(ResourceLocation id, JsonObject json) {
        AltarRecipeInstance i = new AltarRecipeInstance(id);
        for (JsonElement je : JsonUtils.getJsonArray(json, "recipes")) {
            if (!je.isJsonPrimitive() || !je.getAsJsonPrimitive()
                .isString()) {
                continue;
            }
            i.recipeNames.add(new ResourceLocation(je.getAsString()));
        }
        return i;
    }

    public boolean test(AbstractAltarRecipe recipe) {
        return recipeNames.isEmpty() || recipeNames.contains(
            recipe.getNativeRecipe()
                .getRegistryName());
    }

}
