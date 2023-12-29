/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.root;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.PlayerActivityManager;
import shordinger.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ArmaraRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:22
 */
public class ArmaraRootPerk extends RootPerk {

    public ArmaraRootPerk(int x, int y) {
        super("armara", Constellations.armara, x, y);
    }

    // Measure firstmost incoming damage
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHurt(LivingHurtEvent event) {
        Side side = event.entityLiving.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        if (event.entityLiving instanceof EntityPlayer player) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);

            if (PlayerActivityManager.INSTANCE.isPlayerActiveServer(player)) {
                return;
            }

            if (prog.hasPerkEffect(this)) {
                float expGain = event.ammount;
                expGain *= 3F;
                if (event.source.isFireDamage()) {
                    if (player.isInLava()) {
                        expGain *= 0.01F;
                    } else {
                        expGain *= 0.2F;
                    }
                }
                if (event.source == DamageSource.STARVE) {
                    expGain *= 0.1F;
                }
                if (event.source == DamageSource.DROWN) {
                    expGain *= 0.05F;
                }
                if (event.source == DamageSource.CACTUS) {
                    expGain *= 0.01F;
                }
                if (event.source instanceof EntityDamageSource) {
                    expGain *= 1.3F;
                }
                expGain *= expMultiplier;
                expGain = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, expGain);
                expGain = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, expGain);
                expGain = AttributeEvent
                    .postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, expGain);

                float xpGain = expGain;
                LogCategory.PERKS.info(() -> "Grant " + xpGain + " exp to " + player.getDisplayName() + " (Armara)");

                ResearchManager.modifyExp(player, xpGain);
            }
        }
    }

}
