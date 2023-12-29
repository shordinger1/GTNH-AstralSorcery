/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.event.PotionApplyEvent;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributePotionDuration
 * Created by HellFirePvP
 * Date: 26.10.2018 / 23:04
 */
public class AttributePotionDuration extends PerkAttributeType {

    public AttributePotionDuration() {
        super(AttributeTypeRegistry.ATTR_TYPE_POTION_DURATION, true);
    }

    @SubscribeEvent
    public void onPotionDurationNew(PotionApplyEvent.New event) {
        if (event.entityLiving instanceof EntityPlayer) {
            modifyPotionDuration((EntityPlayer) event.entityLiving, event.getPotionEffect(), event.getPotionEffect());
        }
    }

    @SubscribeEvent
    public void onPotionDurationChanged(PotionApplyEvent.Changed event) {
        if (event.entityLiving instanceof EntityPlayer) {
            modifyPotionDuration(
                (EntityPlayer) event.entityLiving,
                event.getNewCombinedEffect(),
                event.getAddedEffect());
        }
    }

    private void modifyPotionDuration(EntityPlayer player, PotionEffect newSetEffect, PotionEffect addedEffect) {
        if (player.world.isRemote || newSetEffect.getPotion()
            .isBadEffect() || addedEffect.getAmplifier() < newSetEffect.getAmplifier()) {
            return;
        }

        float existingDuration = addedEffect.getDuration();
        float newDuration = PerkAttributeHelper.getOrCreateMap(player, Side.SERVER)
            .modifyValue(
                player,
                ResearchManager.getProgress(player, Side.SERVER),
                AttributeTypeRegistry.ATTR_TYPE_POTION_DURATION,
                existingDuration);
        newDuration = AttributeEvent.postProcessModded(player, this, newDuration);

        if (newSetEffect.getDuration() < newDuration) {
            newSetEffect.duration = MathHelper.floor(newDuration);
        }
    }

}
