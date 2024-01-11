/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.network.packet.server;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktCraftingTableFix
 * Created by HellFirePvP
 * Date: 11.08.2016 / 09:47
 */
public class PktCraftingTableFix implements IMessage, IMessageHandler<PktCraftingTableFix, IMessage> {

    private BlockPos at;

    public PktCraftingTableFix() {}

    public PktCraftingTableFix(BlockPos at) {
        this.at = at;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        at = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(at.toLong());
    }

    @Override
    public IMessage onMessage(PktCraftingTableFix message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            openProperCraftingTableGui(message);
        }
        return null;
    }

    // A crafting table that knows its position. useful.
    @SideOnly(Side.CLIENT)
    private void openProperCraftingTableGui(PktCraftingTableFix message) {
        AstralSorcery.proxy.scheduleClientside(() -> ShapedLightProximityRecipe.clientWorkbenchPosition = message.at);
    }

}
