/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.modifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.wrapper.net.minecraft.client.resources.I18n;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierLifeLeech
 * Created by HellFirePvP
 * Date: 28.11.2018 / 09:44
 */
public class AttributeModifierLifeLeech extends PerkAttributeModifier {

    public AttributeModifierLifeLeech(String type, Mode mode, float value) {
        super(type, mode, value);
    }

    @Override
    protected String getUnlocalizedAttributeName() {
        return "perk.attribute.astralsorcery.lifeleech.modifier.name";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        return I18n.format("perk.modifier.format.lifeleech");
    }
}
