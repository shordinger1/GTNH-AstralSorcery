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
 * Class: AttributeTypeMovementSpeed
 * Created by HellFirePvP
 * Date: 08.07.2018 / 21:03
 */
public class AttributeTypeMovementSpeed extends VanillaAttributeType {

    private static final UUID MOVE_SPEED_ADD_ID = UUID.fromString("0E769034-8C58-48A1-88ED-1908F602E127");
    private static final UUID MOVE_SPEED_ADD_MULTIPLY_ID = UUID.fromString("0E769034-8CDD-48A1-88ED-1908F602E127");
    private static final UUID MOVE_SPEED_STACK_MULTIPLY_ID = UUID.fromString("0E769034-8C14-48A1-88ED-1908F602E127");

    public AttributeTypeMovementSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_MOVESPEED);
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.MOVEMENT_SPEED;
    }

    @Override
    public String getDescription() {
        return "Perk MoveSpeed";
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        return getUuid(mode, MOVE_SPEED_ADD_ID, MOVE_SPEED_ADD_MULTIPLY_ID, MOVE_SPEED_STACK_MULTIPLY_ID);
    }

}
