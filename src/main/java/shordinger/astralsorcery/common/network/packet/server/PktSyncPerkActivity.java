/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.PerkEffectHelper;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.log.LogCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncPerkActivity
 * Created by HellFirePvP
 * Date: 09.07.2018 / 17:23
 */
public class PktSyncPerkActivity implements IMessage, IMessageHandler<PktSyncPerkActivity, IMessage> {

    private AbstractPerk perk;
    private boolean unlock;
    private NBTTagCompound newData, oldData;
    private Type type = null;

    public PktSyncPerkActivity() {
    }

    public PktSyncPerkActivity(AbstractPerk perk, boolean unlock) {
        this.perk = perk;
        this.unlock = unlock;
    }

    public PktSyncPerkActivity(Type type) {
        this.type = type;
    }

    public PktSyncPerkActivity(AbstractPerk perk, NBTTagCompound oldData, NBTTagCompound newData) {
        this.type = Type.DATACHANGE;
        this.perk = perk;
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.unlock = buf.readBoolean();
        this.type = ByteBufUtils.readOptional(buf, (byteBuf) -> ByteBufUtils.readEnumValue(byteBuf, Type.class));
        ResourceLocation key = ByteBufUtils.readOptional(buf, ByteBufUtils::readResourceLocation);
        if (key != null) {
            this.perk = PerkTree.PERK_TREE.getPerk(key);
        }
        this.newData = ByteBufUtils.readOptional(buf, ByteBufUtils::readNBTTag);
        this.oldData = ByteBufUtils.readOptional(buf, ByteBufUtils::readNBTTag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.unlock);
        ByteBufUtils.writeOptional(buf, this.type, ByteBufUtils::writeEnumValue);
        ByteBufUtils.writeOptional(
            buf,
            this.perk,
            ((byteBuf, perk) -> ByteBufUtils.writeResourceLocation(byteBuf, perk.getRegistryName())));
        ByteBufUtils.writeOptional(buf, this.newData, ByteBufUtils::writeNBTTag);
        ByteBufUtils.writeOptional(buf, this.oldData, ByteBufUtils::writeNBTTag);
    }

    @Override
    public IMessage onMessage(PktSyncPerkActivity message, MessageContext ctx) {
        handleClientPerkUpdate(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handleClientPerkUpdate(PktSyncPerkActivity pkt) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            if (Minecraft.getMinecraft().thePlayer != null) {
                if (pkt.type != null) {
                    LogCategory.PERKS.info(() -> "Received perk activity packet on clientside: " + pkt.type);
                    switch (pkt.type) {
                        case CLEARALL:
                            PerkEffectHelper.EVENT_INSTANCE.clearAllPerksClient(Minecraft.getMinecraft().thePlayer);
                            break;
                        case UNLOCKALL:
                            PerkEffectHelper.EVENT_INSTANCE.reapplyAllPerksClient(Minecraft.getMinecraft().thePlayer);
                            break;
                        case DATACHANGE:
                            PerkEffectHelper.EVENT_INSTANCE.notifyPerkDataChangeClient(
                                Minecraft.getMinecraft().thePlayer,
                                pkt.perk,
                                pkt.oldData,
                                pkt.newData);
                            break;
                        default:
                            break;
                    }
                } else if (pkt.perk != null) {
                    LogCategory.PERKS.info(
                        () -> "Received perk modification packet on clientside: " + pkt.perk.getRegistryName()
                            + " "
                            + (pkt.unlock ? "Application" : "Removal"));
                    PerkEffectHelper.EVENT_INSTANCE
                        .notifyPerkChange(Minecraft.getMinecraft().thePlayer, Side.CLIENT, pkt.perk, !pkt.unlock);
                }
            }
        });
    }

    public static enum Type {

        CLEARALL,
        UNLOCKALL,
        DATACHANGE

    }
}
