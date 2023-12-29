/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.event.DynamicEnchantmentEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeEnchantmentEffectiveness
 * Created by HellFirePvP
 * Date: 12.08.2018 / 10:22
 */
public class AttributeEnchantmentEffectiveness extends PerkAttributeType {

    public AttributeEnchantmentEffectiveness() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_ENCH_EFFECT, true);
    }

    @SubscribeEvent
    public void onDynEnchantmentModify(DynamicEnchantmentEvent.Modify event) {
        if (event.getResolvedPlayer() != null) {
            EntityPlayer player = event.getResolvedPlayer();
            Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
            if (!hasTypeApplied(player, side)) {
                return;
            }
            float inc = PerkAttributeHelper.getOrCreateMap(player, side)
                .getModifier(
                    player,
                    ResearchManager.getProgress(player, side),
                    AttributeTypeRegistry.ATTR_TYPE_INC_ENCH_EFFECT);
            for (DynamicEnchantment ench : event.getEnchantmentsToApply()) {
                float lvl = ench.getLevelAddition();
                lvl *= inc;
                float post = AttributeEvent.postProcessModded(player, this, lvl);
                ench.setLevelAddition(Math.round(post));
            }
        }
    }

}
