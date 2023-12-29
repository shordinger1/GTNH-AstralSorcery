/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.advancements.instances.ConstellationInstance;
import shordinger.astralsorcery.common.constellation.IConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscoverConstellationTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 10:54
 */
public class DiscoverConstellationTrigger extends ListenerCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "find_constellation");

    public DiscoverConstellationTrigger() {
        super(ID);
    }

    @Override
    public ConstellationInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return ConstellationInstance.deserialize(getId(), json);
    }

    public void trigger(EntityPlayerMP player, IConstellation cst) {
        Listeners<ConstellationInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(cst));
        }
    }

}
