/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeProjectileAttackDamage
 * Created by HellFirePvP
 * Date: 14.07.2018 / 13:11
 */
public class AttributeProjectileAttackDamage extends PerkAttributeType {

    public AttributeProjectileAttackDamage() {
        super(AttributeTypeRegistry.ATTR_TYPE_PROJ_DAMAGE, true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onProjDamage(LivingHurtEvent event) {
        if (event.source.isProjectile()) {
            DamageSource source = event.source;
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getTrueSource();
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                if (!hasTypeApplied(player, side)) {
                    return;
                }

                float amt = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), event.ammount);
                amt = AttributeEvent.postProcessModded(player, this, amt);
                event.setAmount(amt);
            }
        }
    }

}
