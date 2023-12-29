/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.constellation;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkVorux
 * Created by HellFirePvP
 * Date: 20.11.2018 / 21:07
 */
public class PerkVorux extends ConstellationPerk {

    public PerkVorux(int x, int y) {
        super("cst_vorux", Constellations.vorux, x, y);
        setCategory(CATEGORY_FOCUS);
        this.addModifier(
            new PerkAttributeModifier(
                AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT,
                PerkAttributeModifier.Mode.ADDED_MULTIPLY,
                0.01F) {

                @Override
                protected void initModifier() {
                    super.initModifier();

                    this.setAbsolute();
                }

                @Override
                public float getValue(EntityPlayer player, PlayerProgress progress) {
                    return getFlatValue() * (progress.getAppliedPerks()
                        .size()
                        - progress.getSealedPerks()
                        .size());
                }

                @Override
                public boolean hasDisplayString() {
                    return false;
                }
            });
    }

    @SubscribeEvent
    public void onExpGain(AttributeEvent.PostProcessModded ev) {
        if (ev.getType()
            .getTypeString()
            .equals(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP)) {
            EntityPlayer player = ev.getPlayer();
            Side side = player.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                ev.setValue(0);
            }
        }
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
        return super.mayUnlockPerk(progress, player) && !MiscUtils.contains(
            progress.getAppliedPerks(),
            perk -> perk.getCategory()
                .equals(CATEGORY_FOCUS));
    }

}
