/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.base;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.auxiliary.link.ILinkableTile;
import shordinger.astralsorcery.common.starlight.IStarlightReceiver;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.migration.BlockPos;

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
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
    }

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
