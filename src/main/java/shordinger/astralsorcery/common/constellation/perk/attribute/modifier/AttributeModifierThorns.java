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
import net.minecraft.client.resources.I18n;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierThorns
 * Created by HellFirePvP
 * Date: 10.11.2018 / 09:21
 */
public class AttributeModifierThorns extends PerkAttributeModifier {

    public AttributeModifierThorns(String type, Mode mode, float value) {
        super(type, mode, value);
    }

    @Override
    protected String getUnlocalizedAttributeName() {
        return "perk.attribute.astralsorcery.thorns.modifier.name";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        return I18n.format("perk.modifier.format.thorns");
    }

}
