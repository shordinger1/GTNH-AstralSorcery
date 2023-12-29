/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import org.jetbrains.annotations.Nullable;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeAttackSpeed
 * Created by HellFirePvP
 * Date: 14.07.2018 / 07:37
 */
public class AttributeTypeAttackSpeed extends VanillaAttributeType {

    private static final UUID ATTACK_SPEED_ADD_ID = UUID.fromString("79D9A08D-3A36-45CA-BAB9-899ADE702530");
    private static final UUID ATTACK_SPEED_ADD_MULTIPLY_ID = UUID.fromString("79D9AFAA-3A36-45CA-BAB9-899ADE702530");
    private static final UUID ATTACK_SPEED_STACK_MULTIPLY_ID = UUID.fromString("8ED9ABB5-3A36-45CA-BAB9-899ADE702530");

    public AttributeTypeAttackSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_ATTACK_SPEED);
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        return getUuid(mode, ATTACK_SPEED_ADD_ID, ATTACK_SPEED_ADD_MULTIPLY_ID, ATTACK_SPEED_STACK_MULTIPLY_ID);
    }

    @Nullable
    static UUID getUuid(PerkAttributeModifier.Mode mode, UUID attackSpeedAddId, UUID attackSpeedAddMultiplyId, UUID attackSpeedStackMultiplyId) {
        switch (mode) {
            case ADDITION -> {
                return attackSpeedAddId;
            }
            case ADDED_MULTIPLY -> {
                return attackSpeedAddMultiplyId;
            }
            case STACKING_MULTIPLY -> {
                return attackSpeedStackMultiplyId;
            }
            default -> {
            }
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "Perk AttackSpeed";
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.ATTACK_SPEED;
    }
}
