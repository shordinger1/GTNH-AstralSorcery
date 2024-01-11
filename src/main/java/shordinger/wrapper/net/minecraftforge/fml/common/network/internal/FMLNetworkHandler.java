/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.fml.common.network.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.embedded.EmbeddedChannel;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.network.NetworkManager;
import shordinger.wrapper.net.minecraft.network.Packet;
import shordinger.wrapper.net.minecraft.server.management.PlayerList;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.util.FakePlayer;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLContainer;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;
import shordinger.wrapper.net.minecraftforge.fml.common.Loader;
import shordinger.wrapper.net.minecraftforge.fml.common.ModContainer;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLOutboundHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import shordinger.wrapper.net.minecraftforge.fml.common.network.NetworkRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import shordinger.wrapper.net.minecraftforge.fml.common.network.internal.FMLMessage.CompleteHandshake;
import shordinger.wrapper.net.minecraftforge.fml.common.registry.EntityRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

public class FMLNetworkHandler {

    public static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("fml.readTimeout", "30"));
    public static final int LOGIN_TIMEOUT = Integer.parseInt(System.getProperty("fml.loginTimeout", "600"));
    private static EnumMap<Side, FMLEmbeddedChannel> channelPair;

    public static void fmlServerHandshake(PlayerList scm, NetworkManager manager, EntityPlayerMP player) {
        NetworkDispatcher dispatcher = NetworkDispatcher.allocAndSet(manager, scm);
        dispatcher.serverToClientHandshake(player);
    }

    public static void fmlClientHandshake(NetworkManager networkManager) {
        NetworkDispatcher dispatcher = NetworkDispatcher.allocAndSet(networkManager);
        dispatcher.clientToServerHandshake();
    }

    public static void openGui(EntityPlayer entityPlayer, Object mod, int modGuiId, World world, int x, int y, int z) {
        ModContainer mc = FMLCommonHandler.instance()
            .findContainerFor(mod);
        if (entityPlayer instanceof EntityPlayerMP && !(entityPlayer instanceof FakePlayer)) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;
            Container remoteGuiContainer = NetworkRegistry.INSTANCE
                .getRemoteGuiContainer(mc, entityPlayerMP, modGuiId, world, x, y, z);
            if (remoteGuiContainer != null) {
                entityPlayerMP.getNextWindowId();
                entityPlayerMP.closeContainer();
                int windowId = entityPlayerMP.currentWindowId;
                FMLMessage.OpenGui openGui = new FMLMessage.OpenGui(windowId, mc.getModId(), modGuiId, x, y, z);
                EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET)
                    .set(OutboundTarget.PLAYER);
                embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
                    .set(entityPlayerMP);
                embeddedChannel.writeOutbound(openGui);
                entityPlayerMP.openContainer = remoteGuiContainer;
                entityPlayerMP.openContainer.windowId = windowId;
                entityPlayerMP.openContainer.addListener(entityPlayerMP);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                    new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(
                        entityPlayer,
                        entityPlayer.openContainer));
            }
        } else if (entityPlayer instanceof FakePlayer) {
            // NO OP - I won't even log a message!
        } else if (FMLCommonHandler.instance()
            .getSide()
            .equals(Side.CLIENT)) {
            Object guiContainer = NetworkRegistry.INSTANCE
                .getLocalGuiContainer(mc, entityPlayer, modGuiId, world, x, y, z);
            FMLCommonHandler.instance()
                .showGuiScreen(guiContainer);
        } else {
            FMLLog.log.debug(
                "Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUI ID: {},{}",
                mc.getModId(),
                modGuiId);
        }

    }

    @Nullable
    public static Packet<?> getEntitySpawningPacket(Entity entity) {
        EntityRegistration er = EntityRegistry.instance()
            .lookupModSpawn(entity.getClass(), false);
        if (er == null) {
            return null;
        }
        if (er.usesVanillaSpawning()) {
            return null;
        }

        return channelPair.get(Side.SERVER)
            .generatePacketFrom(new FMLMessage.EntitySpawnMessage(er, entity, er.getContainer()));
    }

    @Nullable
    public static String checkModList(FMLHandshakeMessage.ModList modListPacket, Side side) {
        Map<String, String> modList = modListPacket.modList();
        return checkModList(modList, side);
    }

    /**
     * @param listData map of modId string to version string, represents the mods available on the given side
     * @param side     the side that listData is coming from, either client or server
     * @return null if everything is fine, returns a string error message if there are mod rejections
     */
    @Nullable
    public static String checkModList(Map<String, String> listData, Side side) {
        List<Pair<ModContainer, String>> rejects = NetworkRegistry.INSTANCE.registry()
            .entrySet()
            .stream()
            .map(
                entry -> Pair.of(
                    entry.getKey(),
                    entry.getValue()
                        .checkCompatible(listData, side)))
            .filter(pair -> pair.getValue() != null)
            .sorted(
                Comparator.comparing(
                    o -> o.getKey()
                        .getName()))
            .collect(Collectors.toList());
        if (rejects.isEmpty()) {
            return null;
        } else {
            List<String> rejectStrings = new ArrayList<>();
            for (Pair<ModContainer, String> reject : rejects) {
                ModContainer modContainer = reject.getKey();
                rejectStrings.add(modContainer.getName() + ": " + reject.getValue());
            }
            String rejectString = String.join("\n", rejectStrings);
            FMLLog.log.info("Rejecting connection {}: {}", side, rejectString);
            return String.format("Server Mod rejections:\n%s", rejectString);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void addClientHandlers() {
        ChannelPipeline pipeline = channelPair.get(Side.CLIENT)
            .pipeline();
        String targetName = channelPair.get(Side.CLIENT)
            .findChannelHandlerNameForType(FMLRuntimeCodec.class);
        pipeline.addAfter(targetName, "GuiHandler", new OpenGuiHandler());
        pipeline.addAfter(targetName, "EntitySpawnHandler", new EntitySpawnHandler());
    }

    public static void registerChannel(FMLContainer container, Side side) {
        channelPair = NetworkRegistry.INSTANCE
            .newChannel(container, "FML", new FMLRuntimeCodec(), new HandshakeCompletionHandler());
        EmbeddedChannel embeddedChannel = channelPair.get(Side.SERVER);
        embeddedChannel.attr(FMLOutboundHandler.FML_MESSAGETARGET)
            .set(OutboundTarget.NOWHERE);

        if (side == Side.CLIENT) {
            addClientHandlers();
        }
    }

    public static List<FMLProxyPacket> forwardHandshake(CompleteHandshake push, NetworkDispatcher target, Side side) {
        channelPair.get(side)
            .attr(NetworkDispatcher.FML_DISPATCHER)
            .set(target);
        channelPair.get(side)
            .writeOutbound(push);

        ArrayList<FMLProxyPacket> list = new ArrayList<FMLProxyPacket>();
        for (Object o : channelPair.get(side)
            .outboundMessages()) {
            list.add((FMLProxyPacket) o);
        }
        channelPair.get(side)
            .outboundMessages()
            .clear();
        return list;
    }

    public static void enhanceStatusQuery(JsonObject jsonobject) {
        JsonObject fmlData = new JsonObject();
        fmlData.addProperty("type", "FML");
        JsonArray modList = new JsonArray();
        for (ModContainer mc : Loader.instance()
            .getActiveModList()) {
            JsonObject modData = new JsonObject();
            modData.addProperty("modid", mc.getModId());
            modData.addProperty("version", mc.getVersion());
            modList.add(modData);
        }
        fmlData.add("modList", modList);
        jsonobject.add("modinfo", fmlData);
    }
}
