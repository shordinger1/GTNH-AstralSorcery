/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;

import java.util.UUID;

import static shordinger.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeAttackSpeed.getUuid;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMaxReach
 * Created by HellFirePvP
 * Date: 14.07.2018 / 11:06
 */
public class AttributeTypeMaxReach extends VanillaAttributeType {

    private static final UUID REACH_ADD_ID = UUID.fromString("E5416922-E446-4E1B-AEE5-04A6B83E17AA");
    private static final UUID REACH_ADD_MULTIPLY_ID = UUID.fromString("E5DD6922-A49F-4E1B-AEE5-04A6B83E17AA");
    private static final UUID REACH_STACK_MULTIPLY_ID = UUID.fromString("E5DD6922-11D4-4E1B-AEE5-04A6B83E17AA");

    public AttributeTypeMaxReach() {
        super(AttributeTypeRegistry.ATTR_TYPE_REACH);
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        return getUuid(mode, REACH_ADD_ID, REACH_ADD_MULTIPLY_ID, REACH_STACK_MULTIPLY_ID);
    }

    @Override
    public String getDescription() {
        return "Perk MaxReach";
    }

    @Override
    public IAttribute getAttribute() {
        return EntityPlayer.REACH_DISTANCE;
    }
}
