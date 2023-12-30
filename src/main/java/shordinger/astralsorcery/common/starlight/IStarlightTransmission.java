/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.World;

import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightTransmission
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:25
 */
public interface IStarlightTransmission {

    @Nullable
    default public IPrismTransmissionNode getNode() {
        WorldNetworkHandler netHandler = WorldNetworkHandler.getNetworkHandler(getTrWorld());
        return netHandler.getTransmissionNode(getTrPos());
    }

    @Nonnull
    public BlockPos getTrPos();

    @Nonnull
    public World getTrWorld();

    @Nonnull
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at);

}
