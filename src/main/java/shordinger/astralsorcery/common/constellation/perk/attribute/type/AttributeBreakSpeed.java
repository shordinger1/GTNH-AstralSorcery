/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeBreakSpeed
 * Created by HellFirePvP
 * Date: 13.07.2018 / 19:15
 */
public class AttributeBreakSpeed extends PerkAttributeType {

    public static boolean evaluateBreakSpeedWithoutPerks = false;

    public AttributeBreakSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_HARVEST_SPEED);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (evaluateBreakSpeedWithoutPerks) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }
        float speed = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), event.getNewSpeed());
        speed = AttributeEvent.postProcessModded(player, this, speed);
        event.setNewSpeed(speed);
    }

}
