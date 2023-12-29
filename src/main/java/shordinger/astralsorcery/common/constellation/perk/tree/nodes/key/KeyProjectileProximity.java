/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyProjectileDistance
 * Created by HellFirePvP
 * Date: 28.07.2018 / 21:14
 */
public class KeyProjectileProximity extends KeyPerk {

    public KeyProjectileProximity(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onProjDamage(LivingHurtEvent event) {
        if (event.source.isProjectile()) {
            DamageSource source = event.source;
            if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getTrueSource();
                Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
                PlayerProgress prog = ResearchManager.getProgress(player, side);
                if (prog.hasPerkEffect(this)) {
                    float added = 0.75F;
                    added *= PerkAttributeHelper.getOrCreateMap(player, side)
                        .getModifier(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT);

                    float capDstSq = 100; // 10 * 10
                    float dst = -(((float) (player.getDistanceSq(event.entityLiving))) - capDstSq);
                    dst /= capDstSq;
                    if (dst < 0) {
                        dst /= 10; // To make it drop a bit slower though... like. that damage reduction is... not fun
                        // :P
                    }
                    dst = Math.max(0, 1 + dst);
                    added *= dst;

                    float amt = event.ammount;
                    amt *= Math.max(0, added); // Might become negative if too far away; prevent that :P
                    event.setAmount(amt);
                }
            }
        }
    }

}
