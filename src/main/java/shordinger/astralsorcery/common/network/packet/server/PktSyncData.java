/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import static shordinger.astralsorcery.migration.PacketBufferWrapper.readCompoundTag;
import static shordinger.astralsorcery.migration.PacketBufferWrapper.writeCompoundTag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.AbstractData;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import shordinger.astralsorcery.common.util.ByteBufUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncData
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:12
 */
public class PktSyncData implements IMessage, IMessageHandler<PktSyncData, IMessage> {

    private Map<String, AbstractData> data = new HashMap<>();
    private boolean shouldSyncAll = false;

    public PktSyncData() {
    }

    public PktSyncData(Map<String, AbstractData> dataToSend, boolean shouldSyncAll) {
        this.data = dataToSend;
        this.shouldSyncAll = shouldSyncAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        int size = pb.readInt();

        for (int i = 0; i < size; i++) {
            String key = ByteBufUtils.readString(pb);

            byte providerId = pb.readByte();
            AbstractData.AbstractDataProvider<? extends AbstractData> provider = AbstractData.Registry
                .getProvider(providerId);
            if (provider == null) {
                AstralSorcery.log.warn("Provider for ID " + providerId + " doesn't exist! Skipping...");
                continue;
            }

            NBTTagCompound cmp;
            try {
                cmp = readCompoundTag(pb);
            } catch (IOException e) {
                AstralSorcery.log.warn("Provider Compound of " + providerId + " threw an IOException! Skipping...");
                AstralSorcery.log.warn("Exception message: " + e.getMessage());
                continue;
            }

            AbstractData dat = provider.provideNewInstance(Side.CLIENT);
            dat.readRawFromPacket(cmp);

            data.put(key, dat);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(data.size());

        for (String key : data.keySet()) {
            AbstractData dat = data.get(key);
            NBTTagCompound cmp = new NBTTagCompound();
            if (shouldSyncAll) {
                dat.writeAllDataToPacket(cmp);
            } else {
                dat.writeToPacket(cmp);
            }

            ByteBufUtils.writeString(pb, key);

            byte providerId = dat.getProviderID();
            pb.writeByte(providerId);
            writeCompoundTag(pb, cmp);
        }
    }

    @Override
    public IMessage onMessage(PktSyncData message, MessageContext ctx) {
        AstralSorcery.proxy.scheduleClientside(() -> SyncDataHolder.receiveServerPacket(message.data));
        return null;
    }

}
