/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.constellation.perk.AbstractPerk;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTree;
import shordinger.astralsorcery.common.constellation.perk.tree.nodes.GemSlotPerk;
import shordinger.astralsorcery.common.item.gem.ItemPerkGem;
import shordinger.astralsorcery.common.util.ByteBufUtils;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPerkGemModification
 * Created by HellFirePvP
 * Date: 17.11.2018 / 20:50
 */
public class PktPerkGemModification implements IMessageHandler<PktPerkGemModification, IMessage>, IMessage {

    private int action = 0;

    private ResourceLocation perkKey;
    private int slotId = -1;

    public PktPerkGemModification() {}

    public static PktPerkGemModification insertItem(AbstractPerk perk, int slotId) {
        PktPerkGemModification pkt = new PktPerkGemModification();
        pkt.action = 0;
        pkt.perkKey = perk.getRegistryName();
        pkt.slotId = slotId;
        return pkt;
    }

    public static PktPerkGemModification dropItem(AbstractPerk perk) {
        PktPerkGemModification pkt = new PktPerkGemModification();
        pkt.action = 1;
        pkt.perkKey = perk.getRegistryName();
        return pkt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = buf.readInt();
        this.slotId = buf.readInt();
        this.perkKey = ByteBufUtils.readOptional(buf, ByteBufUtils::readResourceLocation);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.action);
        buf.writeInt(this.slotId);
        ByteBufUtils.writeOptional(buf, this.perkKey, ByteBufUtils::writeResourceLocation);
    }

    @Override
    public IMessage onMessage(PktPerkGemModification pkt, MessageContext ctx) {
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .addScheduledTask(() -> {
                AbstractPerk perk = PerkTree.PERK_TREE.getPerk(pkt.perkKey);
                if (perk == null || !(perk instanceof GemSlotPerk)) { // Exclusively for socketable gem perks.
                    return;
                }

                EntityPlayer player = ctx.getServerHandler().player;
                switch (pkt.action) {
                    case 0:
                        ItemStack stack = player.inventory.getStackInSlot(pkt.slotId);
                        ItemStack toInsert = ItemUtils.copyStackWithSize(stack, 1);
                        if (!toInsert.isEmpty() && toInsert.getItem() instanceof ItemPerkGem
                            && !ItemPerkGem.getModifiers(toInsert)
                            .isEmpty()
                            && !((GemSlotPerk) perk).hasItem(player, Side.SERVER)
                            && ((GemSlotPerk) perk).setContainedItem(player, Side.SERVER, toInsert)) {
                            player.inventory.setInventorySlotContents(
                                pkt.slotId,
                                ItemUtils.copyStackWithSize(stack, stack.getCount() - 1));
                        }
                        break;
                    case 1:
                        if (((GemSlotPerk) perk).hasItem(player, Side.SERVER)) {
                            ((GemSlotPerk) perk).dropItemToPlayer(player);
                        }
                        break;
                    default:
                        break;
                }
            });
        return null;
    }

}
