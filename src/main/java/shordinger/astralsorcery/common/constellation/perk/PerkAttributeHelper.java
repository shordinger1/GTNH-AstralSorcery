/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeHelper
 * Created by HellFirePvP
 * Date: 08.07.2018 / 11:00
 */
public class PerkAttributeHelper {

    private static Map<UUID, PlayerAttributeMap> playerPerkAttributes = new HashMap<>();
    private static Map<UUID, PlayerAttributeMap> playerPerkAttributesClient = new HashMap<>();

    private PerkAttributeHelper() {
    }

    @Nonnull
    public static PlayerAttributeMap getOrCreateMap(EntityPlayer player, Side side) {
        if (side == Side.CLIENT) {
            return playerPerkAttributesClient
                .computeIfAbsent(player.getUniqueID(), (uuid) -> new PlayerAttributeMap(side));
        } else {
            return playerPerkAttributes.computeIfAbsent(player.getUniqueID(), (uuid) -> new PlayerAttributeMap(side));
        }
    }

    public static PlayerAttributeMap getMockInstance(Side side) {
        return new PlayerAttributeMap(side);
    }

    @SideOnly(Side.CLIENT)
    public static void clearClient() {
        playerPerkAttributesClient.clear();
    }

    public static void clearServer() {
        playerPerkAttributes.clear();
    }

}
