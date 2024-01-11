/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event.listener;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import shordinger.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktFinalizeLogin;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerNetwork
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:10
 */
public class EventHandlerNetwork {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        EntityPlayerMP p = (EntityPlayerMP) e.player;
        AstralSorcery.log.info("[Astral Sorcery] Waiting for server synchronization on login for " + p.getName() + "...");
        AstralSorcery.proxy.scheduleDelayed(() -> {
            AstralSorcery.log.info("[Astral Sorcery] Synchronizing baseline information to " + p.getName());
            ResearchManager.sendInitClientKnowledge(p);
            CelestialGatewaySystem.instance.syncTo(p);
            SyncDataHolder.syncAllDataTo(p);

            PacketChannel.CHANNEL.sendTo(new PktFinalizeLogin(), p);
        });
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        EntityPlayer player = e.player;

        PlayerChargeHandler.INSTANCE.informDisconnect(player);
        EventHandlerEntity.attackStack.remove(e.player.getEntityId());
        //ResearchManager.logoutResetClient(player);
    }

}
