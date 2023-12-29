/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.crafting;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.data.research.ResearchProgression;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IProgressionGatedRecipe
 * Created by HellFirePvP
 * Date: 17.10.2016 / 14:33
 */
public interface IGatedRecipe {

    public boolean hasProgressionServer(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    public boolean hasProgressionClient();

    public interface Progression extends IGatedRecipe {

        @Nonnull
        ResearchProgression getRequiredProgression();

        default public boolean hasProgressionServer(EntityPlayer player) {
            return !ResearchManager.getProgress(player, Side.SERVER)
                .getResearchProgression()
                .contains(getRequiredProgression());
        }

        @SideOnly(Side.CLIENT)
        default public boolean hasProgressionClient() {
            return ResearchManager.clientProgress.getResearchProgression()
                .contains(getRequiredProgression());
        }

    }

}
