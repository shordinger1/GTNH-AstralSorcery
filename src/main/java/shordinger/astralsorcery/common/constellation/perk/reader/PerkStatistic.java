/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.reader;

import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkStatistic
 * Created by HellFirePvP
 * Date: 19.01.2019 / 10:31
 */
public class PerkStatistic {

    private final PerkAttributeType type;
    private final String unlocPerkTypeName;
    private final String perkValue;
    private final String suffix;
    private final String postProcessInfo;

    public PerkStatistic(PerkAttributeType type, String perkValue, String suffix, String postProcessInfo) {
        this.type = type;
        this.unlocPerkTypeName = type.getUnlocalizedName();
        this.perkValue = perkValue;
        this.suffix = suffix;
        this.postProcessInfo = postProcessInfo;
    }

    public PerkAttributeType getType() {
        return type;
    }

    public String getUnlocPerkTypeName() {
        return unlocPerkTypeName;
    }

    public String getPerkValue() {
        return perkValue;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getPostProcessInfo() {
        return postProcessInfo;
    }
}
