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
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DiscidiaRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:11
 */
public class DiscidiaRootPerk extends RootPerk {

    public DiscidiaRootPerk(int x, int y) {
        super("discidia", Constellations.discidia, x, y);
    }

    // Measure actual outcome of dmg
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamage(LivingDamageEvent event) {
        Side side = event.entityLiving.world.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        DamageSource ds = event.source;
        EntityPlayer player = null;
        if (ds.getImmediateSource() != null && ds.getImmediateSource() instanceof EntityPlayer) {
            player = (EntityPlayer) ds.getImmediateSource();
        }
        if (player == null && ds.getTrueSource() != null && ds.getTrueSource() instanceof EntityPlayer) {
            player = (EntityPlayer) ds.getTrueSource();
        }
        if (player != null) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float dmgDealt = event.ammount;
                dmgDealt *= 0.09F;
                dmgDealt *= expMultiplier;
                dmgDealt = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, dmgDealt);
                dmgDealt = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, dmgDealt);
                dmgDealt = AttributeEvent
                    .postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, dmgDealt);

                float expGain = dmgDealt;
                EntityPlayer logPlayer = player;
                LogCategory.PERKS.info(() -> "Grant " + expGain + " exp to " + logPlayer.getName() + " (Discidia)");

                ResearchManager.modifyExp(player, expGain);
            }
        }
    }

}
