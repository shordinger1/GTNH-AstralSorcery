/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;
import shordinger.astralsorcery.common.starlight.IStarlightSource;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionSource;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionSourceNode
 * Created by HellFirePvP
 * Date: 04.08.2016 / 14:59
 */
public class SimpleTransmissionSourceNode extends SimplePrismTransmissionNode implements ITransmissionSource {

    public SimpleTransmissionSourceNode(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public IIndependentStarlightSource provideNewIndependentSource(IStarlightSource source) {
        return source.provideNewSourceNode();
    }

    @Override
    public TransmissionClassRegistry.TransmissionProvider getProvider() {
        return new Provider();
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new SimpleTransmissionSourceNode(null);
        }

        @Override
        public String getIdentifier() {
            return Tags.MODID + ":SimpleTransmissionSourceNode";
        }

    }

}
