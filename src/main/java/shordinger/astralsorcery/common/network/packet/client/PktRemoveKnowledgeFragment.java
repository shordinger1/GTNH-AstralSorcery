/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.client;

import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRemoveKnowledgeFragment
 * Created by HellFirePvP
 * Date: 27.10.2018 / 23:38
 */
public class PktRemoveKnowledgeFragment implements IMessage, IMessageHandler<PktRemoveKnowledgeFragment, IMessage> {

    private int index = 0;

    public PktRemoveKnowledgeFragment() {}

    public PktRemoveKnowledgeFragment(int index) {
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.index);
    }

    @Override
    public IMessage onMessage(PktRemoveKnowledgeFragment pkt, MessageContext ctx) {
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .addScheduledTask(() -> {
                EntityPlayer pl = ctx.getServerHandler().player;
                if (pl != null) {
                    ItemStack stack = pl.inventory.getStackInSlot(pkt.index);
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemKnowledgeFragment) {
                        pl.inventory.setInventorySlotContents(pkt.index, ItemStack.EMPTY); // Remove that fragment.
                    }
                }
            });
        return null;
    }
}
