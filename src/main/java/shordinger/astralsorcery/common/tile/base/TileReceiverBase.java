/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import shordinger.astralsorcery.common.auxiliary.link.ILinkableTile;
import shordinger.astralsorcery.common.starlight.IStarlightReceiver;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileReceiverBase
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:47
 */
public abstract class TileReceiverBase extends TileNetwork implements IStarlightReceiver, ILinkableTile {

    @Override
    public World getLinkWorld() {
        return getTrWorld();
    }

    @Override
    public BlockPos getLinkPos() {
        return getTrPos();
    }

    @Override
    @Nonnull
    public BlockPos getTrPos() {
        return getPos();
    }

    @Override
    @Nonnull
    public World getTrWorld() {
        return getWorld();
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {}

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        return false;
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return new LinkedList<>();
    }

    @Nullable
    public <T extends ITransmissionReceiver> T tryGetNode() {
        IPrismTransmissionNode node = WorldNetworkHandler.getNetworkHandler(world)
            .getTransmissionNode(getPos());
        if (node == null || !(node instanceof ITransmissionReceiver)) return null;
        return (T) node;
    }

}
