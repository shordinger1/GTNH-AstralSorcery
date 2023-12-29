/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeLifeRecovery
 * Created by HellFirePvP
 * Date: 20.07.2018 / 17:22
 */
public class AttributeLifeRecovery extends PerkAttributeType {

    public AttributeLifeRecovery() {
        super(AttributeTypeRegistry.ATTR_TYPE_LIFE_RECOVERY, true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHeal(LivingHealEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float heal = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), event.ammount);
        heal = AttributeEvent.postProcessModded(player, this, heal);
        float val = heal;
        if (val <= 0) {
            event.setCanceled(true);
        } else {
            event.setAmount(val);
        }
    }

}
