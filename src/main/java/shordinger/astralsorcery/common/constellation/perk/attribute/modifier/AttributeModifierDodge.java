/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.modifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierDodge
 * Created by HellFirePvP
 * Date: 19.01.2019 / 13:59
 */
public class AttributeModifierDodge extends PerkAttributeModifier {

    public AttributeModifierDodge(String type, Mode mode, float value) {
        super(type, mode, value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        String str = super.getLocalizedAttributeValue();
        if (getMode() == Mode.ADDITION) {
            str += "%";
        }
        return str;
    }
}
