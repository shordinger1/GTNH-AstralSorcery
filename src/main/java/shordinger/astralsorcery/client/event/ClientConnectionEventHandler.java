/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.event;

import java.util.HashMap;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ChatAllowedCharacters;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.ClientProxy;
import shordinger.astralsorcery.client.data.PersistentDataManager;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.gui.GuiJournalProgression;
import shordinger.astralsorcery.client.render.tile.TESRTranslucentBlock;
import shordinger.astralsorcery.client.util.ClientScreenshotCache;
import shordinger.astralsorcery.client.util.UISextantCache;
import shordinger.astralsorcery.client.util.camera.ClientCameraManager;
import shordinger.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import shordinger.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.perk.PerkEffectHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.data.DataLightBlockEndpoints;
import shordinger.astralsorcery.common.data.DataLightConnections;
import shordinger.astralsorcery.common.data.DataPatreonFlares;
import shordinger.astralsorcery.common.data.DataTimeFreezeEffects;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.util.PlayerActivityManager;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientConnectionEventHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 13:05
 */
public class ClientConnectionEventHandler {

    // Used to cleanup stuff on clientside to make the client functional to switch servers at any time.
    @SubscribeEvent
    public void onDc(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        AstralSorcery.log.info("Cleaning client cache...");
        EffectHandler.cleanUp();
        ClientCameraManager.getInstance()
            .removeAllAndCleanup();
        ConstellationSkyHandler.getInstance()
            .clientClearCache();
        GuiJournalProgression.resetJournal(); // Refresh journal gui
        ResearchManager.clientProgress = new PlayerProgress();
        ResearchManager.clientInitialized = false;
        AstralSorcery.proxy.scheduleClientside(ClientScreenshotCache::cleanUp);
        ClientRenderEventHandler.resetPermChargeReveal();
        ClientRenderEventHandler.resetTempChargeReveal();
        AstralSorcery.proxy.scheduleClientside(TESRTranslucentBlock::cleanUp);
        PlayerChargeHandler.INSTANCE.setClientCharge(0F);
        PerkEffectHelper.perkCooldownsClient.clear();
        CelestialGatewaySystem.instance.updateClientCache(new HashMap<>());
        AttributeTypeRegistry.getTypes()
            .forEach(t -> t.clear(Side.CLIENT));
        PerkTree.PERK_TREE.clearCache(Side.CLIENT);
        UISextantCache.INSTANCE.clearClient();
        PlayerActivityManager.INSTANCE.clearCache(Side.CLIENT);
        ((DataLightConnections) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS)).clientClean();
        ((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS))
            .clientClean();
        ((DataTimeFreezeEffects) SyncDataHolder.getDataClient(SyncDataHolder.DATA_TIME_FREEZE_EFFECTS)).clientClean();
        ((DataPatreonFlares) SyncDataHolder.getDataClient(SyncDataHolder.DATA_PATREON_FLARES)).cleanUp(Side.CLIENT);
        PersistentDataManager.INSTANCE.clearCreative();
        ClientProxy.connected = false;
        AstralSorcery.log.info("Cleared cached client data! Disconnected from server.");
    }

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            NetworkManager nm = event.getManager();
            String addr = nm.getRemoteAddress()
                .toString();
            if (nm.isLocalChannel()) {
                IntegratedServer is = Minecraft.getMinecraft()
                    .getIntegratedServer();
                if (is != null) {
                    addr = is.getWorldName();
                }
            } else {
                int id = addr.indexOf('\\');
                if (id != -1) {
                    addr = addr.substring(0, MathHelper.clamp(id, 1, addr.length()));
                }
            }
            addr = sanitizeFileName(addr);
            ClientScreenshotCache.loadAndInitScreenshotsFor(addr);
        });
    }

    private String sanitizeFileName(String addr) {
        addr = addr.trim();
        addr = addr.replace(' ', '_');
        addr = addr.toLowerCase();
        for (char c0 : ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS) {
            addr = addr.replace(c0, '_');
        }
        addr = addr.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"); // Anything else that falls through
        return addr;
    }

}
