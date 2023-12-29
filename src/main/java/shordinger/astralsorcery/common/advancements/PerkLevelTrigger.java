/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.advancements.instances.PerkLevelInstance;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkLevelTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 13:18
 */
public class PerkLevelTrigger extends ListenerCriterionTrigger<PerkLevelInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "perk_level");

    public PerkLevelTrigger() {
        super(ID);
    }

    @Override
    public PerkLevelInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return PerkLevelInstance.deserialize(getId(), json);
    }

    public void trigger(EntityPlayerMP player) {
        Listeners<PerkLevelInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(player));
        }
    }

}
