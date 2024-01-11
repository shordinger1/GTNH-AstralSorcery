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
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingHurtEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeAllElementalResist
 * Created by HellFirePvP
 * Date: 13.07.2018 / 23:34
 */
public class AttributeAllElementalResist extends PerkAttributeType {

    public AttributeAllElementalResist() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST, true);
    }

    @SubscribeEvent
    public void onDamageTaken(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (!hasTypeApplied(player, side)) {
            return;
        }
        DamageSource ds = event.getSource();
        if (isMaybeElementalDamage(ds)) {
            float multiplier = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchManager.getProgress(player, side), getTypeString(), 1F);
            multiplier -= 1F;
            multiplier = AttributeEvent.postProcessModded(player, this, multiplier);
            multiplier = 1F - MathHelper.clamp(multiplier, 0F, 1F);
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    private boolean isMaybeElementalDamage(DamageSource source) {
        // "Magic" is often used for any kinds of damages... poison for example
        if (source.isFireDamage() || source.isMagicDamage()) {
            return true;
        }
        String key = source.getDamageType();
        if (key == null) {
            return false;
        }
        key = key.toLowerCase();
        return key.contains("fire") || key.contains("heat")
            || key.contains("lightning")
            || key.contains("cold")
            || key.contains("freez")
            || key.contains("discharg")
            || key.contains("electr")
            || key.contains("froze")
            || key.contains("ice");
    }

}
