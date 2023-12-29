/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.advancements.instances.ConstellationInstance;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttuneSelfTrigger
 * Created by HellFirePvP
 * Date: 27.10.2018 / 13:05
 */
public class AttuneSelfTrigger extends ListenerCriterionTrigger<ConstellationInstance> {

    public static final ResourceLocation ID = new ResourceLocation(AstralSorcery.MODID, "attune_self");

    public AttuneSelfTrigger() {
        super(ID);
    }

    @Override
    public ConstellationInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return ConstellationInstance.deserialize(getId(), json);
    }

    public void trigger(EntityPlayerMP player, IMajorConstellation attuned) {
        Listeners<ConstellationInstance> listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger((i) -> i.test(attuned));
        }
    }

}
