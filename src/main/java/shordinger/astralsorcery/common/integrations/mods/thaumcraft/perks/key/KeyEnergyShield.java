/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations.mods.thaumcraft.perks.key;

import java.util.Collection;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.integrations.mods.thaumcraft.perks.KeyPerkThaumcraft;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyEnergyShield
 * Created by HellFirePvP
 * Date: 18.11.2018 / 22:42
 */
public class KeyEnergyShield extends KeyPerkThaumcraft {

    public KeyEnergyShield(String name, int x, int y) {
        super(name, x, y);
    }

    @SubscribeEvent
    public void on(AttributeEvent.PostProcessVanilla event) {
        EntityPlayer owner = event.getPlayer();
        if (owner != null && event.getAttribute()
            .equals(SharedMonsterAttributes.MAX_HEALTH)) {
            if (owner.getEntityWorld() == null) {
                return; // Srsly, fck you ExU2. do your stuff correlty for once.
            }
            Side side = owner.getEntityWorld().isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(owner, side);
            if (prog.hasPerkEffect(this)) {
                event.setValue(1);
            }
        }
    }

    @Override
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        super.addLocalizedTooltip(tooltip);
        return false;
    }
}
