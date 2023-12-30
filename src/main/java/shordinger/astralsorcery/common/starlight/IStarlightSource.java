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
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionSource;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightSource
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:23
 */
public interface IStarlightSource extends IStarlightTransmission {

    @Nonnull
    public IIndependentStarlightSource provideNewSourceNode();

    @Nonnull
    public ITransmissionSource provideSourceNode(BlockPos at);

    public boolean needToUpdateStarlightSource();

    public void markUpdated();

    @Override
    @Nonnull
    default public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return provideSourceNode(at);
    }

}
