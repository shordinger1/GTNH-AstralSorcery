/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.constellation.perk.attribute.modifier.AttributeModifierLifeLeech;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingDamageEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeLifeLeech
 * Created by HellFirePvP
 * Date: 01.08.2018 / 21:04
 */
public class AttributeLifeLeech extends PerkAttributeType {

    public AttributeLifeLeech() {
        super(AttributeTypeRegistry.ATTR_TYPE_ATTACK_LIFE_LEECH);
    }

    @Nonnull
    @Override
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        return new AttributeModifierLifeLeech(getTypeString(), mode, modifier);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            if (side == Side.SERVER && hasTypeApplied(player, side)) {
                float leechPerc = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(
                        player,
                        ResearchManager.getProgress(player, side),
                        AttributeTypeRegistry.ATTR_TYPE_ATTACK_LIFE_LEECH,
                        0F);
                leechPerc /= 100.0F;
                leechPerc = AttributeEvent.postProcessModded(player, this, leechPerc);
                if (leechPerc > 0) {
                    float leech = event.getAmount() * leechPerc;
                    if (leech > 0) {
                        player.heal(leech);
                    }
                }
            }
        }
    }

}
