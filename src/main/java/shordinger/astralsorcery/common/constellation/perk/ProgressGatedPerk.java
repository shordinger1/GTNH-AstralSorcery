/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk;

import java.util.Collection;
import java.util.function.BiFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.data.research.ResearchProgression;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProgressGatedPerk
 * Created by HellFirePvP
 * Date: 24.11.2018 / 16:16
 */
public class ProgressGatedPerk extends AbstractPerk {

    private BiFunction<EntityPlayer, PlayerProgress, Boolean> unlockFunction = (player, progress) -> true;

    public ProgressGatedPerk(String name, int x, int y) {
        super(name, x, y);
    }

    public ProgressGatedPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    public void setRequireDiscoveredConstellation(IConstellation cst) {
        addResearchPreRequisite((player, progress) -> progress.hasConstellationDiscovered(cst));
    }

    public void addRequireProgress(ResearchProgression progression) {
        addResearchPreRequisite(
            ((player, progress) -> progress.getResearchProgression()
                .contains(progression)));
    }

    public void addRequireTier(ProgressionTier tier) {
        addResearchPreRequisite(
            ((player, progress) -> progress.getTierReached()
                .isThisLaterOrEqual(tier)));
    }

    public void addResearchPreRequisite(BiFunction<EntityPlayer, PlayerProgress, Boolean> unlockFunction) {
        BiFunction<EntityPlayer, PlayerProgress, Boolean> prev = this.unlockFunction;
        this.unlockFunction = (player, progress) -> prev.apply(player, progress)
            && unlockFunction.apply(player, progress);
        disableTooltipCaching(); // Cannot cache as it may change.
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
        if (!canSee(player, progress)) {
            return false;
        }
        return super.mayUnlockPerk(progress, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        if (!canSeeClient()) {
            tooltip.add(TextFormatting.RED + I18n.format("perk.info.missing_progress"));
            return false;
        }
        return super.addLocalizedTooltip(tooltip);
    }

    @SideOnly(Side.CLIENT)
    public final boolean canSeeClient() {
        return canSee(Minecraft.getMinecraft().thePlayer, Side.CLIENT);
    }

    public final boolean canSee(EntityPlayer player, Side side) {
        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (prog.isValid()) {
            return canSee(player, prog);
        }
        return false;
    }

    public final boolean canSee(EntityPlayer player, PlayerProgress progress) {
        return unlockFunction.apply(player, progress);
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {
    }

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {
    }

}
