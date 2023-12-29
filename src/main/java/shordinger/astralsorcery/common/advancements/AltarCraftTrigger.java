/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.advancements.instances.AltarRecipeInstance;
import shordinger.astralsorcery.common.crafting.altar.AbstractAltarRecipe;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarCraftTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 14:23
 */
public class AltarCraftTrigger extends ListenerCriterionTrigger<AltarRecipeInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "altar_craft");

    public AltarCraftTrigger() {
        super(ID);
    }

    @Override
    public AltarRecipeInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return AltarRecipeInstance.deserialize(getId(), json);
    }

    public void trigger(EntityPlayerMP player, AbstractAltarRecipe recipe) {
        Listeners<AltarRecipeInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(recipe));
        }
    }

}
