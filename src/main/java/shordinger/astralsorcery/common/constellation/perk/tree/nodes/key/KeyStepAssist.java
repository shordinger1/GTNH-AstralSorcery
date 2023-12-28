/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes.key;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkEffectHelper;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import shordinger.astralsorcery.common.constellation.perk.types.ICooldownPerk;
import shordinger.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktSyncStepAssist;
import shordinger.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyStepAssist
 * Created by HellFirePvP
 * Date: 02.08.2018 / 22:54
 */
public class KeyStepAssist extends KeyPerk implements IPlayerTickPerk, ICooldownPerk {

    public KeyStepAssist(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER) {
            if (!PerkEffectHelper.EVENT_INSTANCE.isCooldownActiveForPlayer(player, this)) {
                player.stepHeight += 0.5F;
            } else {
                if (player.stepHeight < 1.1F) {
                    player.stepHeight = 1.1F;
                }
            }
            PerkEffectHelper.EVENT_INSTANCE.forceSetCooldownForPlayer(player, this, 20);
            if (MiscUtils.isConnectionEstablished((EntityPlayerMP) player)) {
                PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
                PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void handleCooldownTimeout(EntityPlayer player) {
        player.stepHeight -= 0.5F;
        if (player.stepHeight < 0.6F) {
            player.stepHeight = 0.6F;
        }

        if (player instanceof EntityPlayerMP && MiscUtils.isConnectionEstablished((EntityPlayerMP) player)) {
            PktSyncStepAssist sync = new PktSyncStepAssist(player.stepHeight);
            PacketChannel.CHANNEL.sendTo(sync, (EntityPlayerMP) player);
        }
    }

}
