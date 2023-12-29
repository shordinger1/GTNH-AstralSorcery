/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeArmor
 * Created by HellFirePvP
 * Date: 08.07.2018 / 23:34
 */
public class AttributeTypeArmor extends VanillaAttributeType {

    private static final UUID ARMOR_ADD_ID = UUID.fromString("92AAF3D7-D1CD-44CD-A721-7975FBFDB763");
    private static final UUID ARMOR_ADD_MULTIPLY_ID = UUID.fromString("92AAF3D7-C4CD-44CD-A721-7975FBFDB763");
    private static final UUID ARMOR_STACK_MULTIPLY_ID = UUID.fromString("92AAF3D7-FF4D-44CD-A721-7975FBFDB763");

    public AttributeTypeArmor() {
        super(AttributeTypeRegistry.ATTR_TYPE_ARMOR);
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.ARMOR;
    }

    @Override
    public String getDescription() {
        return "Perk Armor";
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        switch (mode) {
            case ADDITION:
                return ARMOR_ADD_ID;
            case ADDED_MULTIPLY:
                return ARMOR_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return ARMOR_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }
}
