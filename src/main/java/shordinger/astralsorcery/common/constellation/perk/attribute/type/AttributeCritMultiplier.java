/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraftforge.event.entity.EntityJoinWorldEvent;
import shordinger.wrapper.net.minecraftforge.event.entity.player.CriticalHitEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeCritMultiplier
 * Created by HellFirePvP
 * Date: 13.07.2018 / 19:22
 */
public class AttributeCritMultiplier extends PerkAttributeType {

    public AttributeCritMultiplier() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_MULTIPLIER, true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onArrowCt(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getEntity();
            if (!arrow.getIsCritical()) return; //No crit

            if (arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float dmgMod = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
                dmgMod = AttributeEvent.postProcessModded(player, this, dmgMod);
                arrow.setDamage(arrow.getDamage() * dmgMod);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCrit(CriticalHitEvent event) {
        if (!event.isVanillaCritical() && event.getResult() != Event.Result.ALLOW) {
            return; //No crit
        }

        EntityPlayer player = event.getEntityPlayer();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float dmgMod = PerkAttributeHelper.getOrCreateMap(event.getEntityPlayer(), side)
                .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
        dmgMod = AttributeEvent.postProcessModded(player, this, dmgMod);
        event.setDamageModifier(event.getDamageModifier() * dmgMod);
    }

}
