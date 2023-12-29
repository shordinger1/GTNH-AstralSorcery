/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.root;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMajorConstellation;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreePointConstellation;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootPerk
 * Created by HellFirePvP
 * Date: 08.07.2018 / 10:25
 */
public class RootPerk extends AttributeModifierPerk {

    protected float expMultiplier = 1.0F;
    private final IMajorConstellation constellation;

    public RootPerk(String name, IMajorConstellation constellation, int x, int y) {
        super(name, x, y);
        setCategory(AbstractPerk.CATEGORY_ROOT);
        setRequireDiscoveredConstellation(constellation);
        this.constellation = constellation;
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, "root_" + name) {

            @Override
            public void loadFromConfig(Configuration cfg) {
                expMultiplier = cfg.getFloat(
                    "Exp_Multiplier",
                    getConfigurationSection(),
                    expMultiplier,
                    0F,
                    1024F,
                    "Sets the exp multiplier exp gained from this root-perk are multiplied by. (So higher multiplier -> more exp)");

                RootPerk.this.loadAdditionalConfigurations(cfg);
            }
        });
    }

    @Override
    protected PerkTreePoint<? extends RootPerk> initPerkTreePoint() {
        return new PerkTreePointConstellation<>(
            this,
            getOffset(),
            constellation,
            PerkTreePointConstellation.ROOT_SPRITE_SIZE);
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        expMultiplier *= multiplier;
    }

    protected void loadAdditionalConfigurations(Configuration cfg) {
    }

    public IConstellation getConstellation() {
        return constellation;
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
        if (progress.hasFreeAllocationPoint(player) && canSee(player, progress)) {
            AbstractPerk core = PerkTree.PERK_TREE.getAstralSorceryPerk("core");
            if (core != null && progress.hasPerkEffect(core)) {
                return true;
            }
        }

        return super.mayUnlockPerk(progress, player);
    }
}
