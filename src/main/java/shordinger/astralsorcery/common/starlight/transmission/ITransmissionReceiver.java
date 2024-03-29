/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission;

import java.util.LinkedList;
import java.util.List;

import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITransmissionReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:43
 */
public interface ITransmissionReceiver extends IPrismTransmissionNode {

    @Override
    default public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler) {
        return new LinkedList<>();
    }

    @Override
    default public void notifyLink(World world, BlockPos to) {}

    @Override
    default public boolean notifyUnlink(World world, BlockPos to) {
        return false;
    }

    public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount);

}
