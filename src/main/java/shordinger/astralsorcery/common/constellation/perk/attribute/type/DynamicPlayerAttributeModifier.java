/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute.type;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicPlayerAttributeModifier
 * Created by HellFirePvP
 * Date: 08.07.2018 / 15:33
 */
public class DynamicPlayerAttributeModifier extends AttributeModifier {

    private EntityPlayer player;
    private Side side;
    private String type;

    public DynamicPlayerAttributeModifier(UUID idIn, String nameIn, String typeIn, PerkAttributeModifier.Mode mode,
                                          EntityPlayer player, Side side) {
        this(idIn, nameIn, typeIn, mode.getVanillaAttributeOperation(), player, side);
    }

    public DynamicPlayerAttributeModifier(UUID idIn, String nameIn, String typeIn, int operationIn, EntityPlayer player,
                                          Side side) {
        super(idIn, nameIn, 0, operationIn);
        this.setSaved(false);
        this.player = player;
        this.side = side;
        this.type = typeIn;
    }

    @Override
    public double getAmount() {
        PerkAttributeModifier.Mode mode = PerkAttributeModifier.Mode.fromVanillaAttributeOperation(getOperation());
        return PerkAttributeHelper.getOrCreateMap(player, side)
            .getModifier(player, ResearchManager.getProgress(player, side), type, mode) - 1;
    }
}
