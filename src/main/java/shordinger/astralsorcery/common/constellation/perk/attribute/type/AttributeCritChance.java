/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.constellation.perk.attribute.modifier.AttributeModifierCritChance;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeCritChance
 * Created by HellFirePvP
 * Date: 13.07.2018 / 19:06
 */
public class AttributeCritChance extends PerkAttributeType {

    public AttributeCritChance() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_CHANCE);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new AttributeModifierCritChance(getTypeString(), mode, modifier);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onArrowCt(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) event.getEntity();
            if (arrow.shootingEntity != null && arrow.shootingEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }
                float critChance = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 0F);
                critChance = AttributeEvent.postProcessModded(player, this, critChance);
                critChance /= 100.0F;
                if (critChance >= rand.nextFloat()) {
                    arrow.setIsCritical(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onCrit(CriticalHitEvent event) {
        if (event.isVanillaCritical() || event.getResult() == Event.Result.ALLOW) {
            return;
        }
        EntityPlayer player = event.getEntityPlayer();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }

        float critChance = PerkAttributeHelper.getOrCreateMap(player, side)
            .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 0F);
        critChance = AttributeEvent.postProcessModded(player, this, critChance);
        critChance /= 100.0F;
        if (critChance >= rand.nextFloat()) {
            event.setResult(Event.Result.ALLOW);
        }
    }

}
