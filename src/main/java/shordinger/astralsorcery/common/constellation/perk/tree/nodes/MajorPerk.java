/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes;

import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreeMajor;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreePoint;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MajorPerk
 * Created by HellFirePvP
 * Date: 17.07.2018 / 18:54
 */
public class MajorPerk extends AttributeModifierPerk {

    public MajorPerk(String name, int x, int y) {
        super(name, x, y);
        setCategory(CATEGORY_MAJOR);
    }

    @Override
    protected PerkTreePoint<? extends MajorPerk> initPerkTreePoint() {
        return new PerkTreeMajor<>(this, this.getOffset());
    }
}
