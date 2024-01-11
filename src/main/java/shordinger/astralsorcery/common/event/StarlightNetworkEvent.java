/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event;

import shordinger.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkEvent
 * Created by HellFirePvP
 * Date: 23.10.2016 / 22:34
 */
//Use this in case you want to add transmission or source nodes to the starlight network system.
public class StarlightNetworkEvent {

    public static class TransmissionRegister extends Event {

        private final TransmissionClassRegistry registry;

        public TransmissionRegister(TransmissionClassRegistry registry) {
            this.registry = registry;
        }

        public TransmissionClassRegistry getRegistry() {
            return registry;
        }
    }

    public static class SourceProviderRegistry extends Event {

        private final SourceClassRegistry registry;

        public SourceProviderRegistry(SourceClassRegistry registry) {
            this.registry = registry;
        }

        public SourceClassRegistry getRegistry() {
            return registry;
        }

    }

}
