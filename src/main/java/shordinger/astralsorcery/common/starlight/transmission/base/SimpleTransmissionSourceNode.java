/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;
import shordinger.astralsorcery.common.starlight.IStarlightSource;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionSource;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;

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
            return AstralSorcery.MODID + ":SimpleTransmissionSourceNode";
        }

    }

}
