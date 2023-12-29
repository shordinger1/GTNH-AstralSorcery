/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:43
 */
public interface IStarlightReceiver extends IStarlightTransmission {

    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at);

    @Override
    @Nonnull
    default public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return provideEndpoint(at);
    }
}
