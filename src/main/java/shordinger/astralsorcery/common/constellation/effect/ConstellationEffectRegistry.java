/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.constellation.effect.aoe.*;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.event.APIRegistryEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectRegistry
 * Created by HellFirePvP
 * Date: 01.10.2016 / 15:49
 */
public class ConstellationEffectRegistry {

    private static Map<IWeakConstellation, ConstellationEffectProvider> providerMap = new HashMap<>();
    private static Map<IWeakConstellation, ConstellationEffect> singleRenderInstances = new HashMap<>();

    public static void init() {
        register(Constellations.aevitas, CEffectAevitas::new);
        register(Constellations.discidia, CEffectDiscidia::new);
        register(Constellations.armara, CEffectArmara::new);
        register(Constellations.vicio, CEffectVicio::new);
        register(Constellations.evorsio, CEffectEvorsio::new);

        register(Constellations.mineralis, CEffectMineralis::new);
        register(Constellations.lucerna, CEffectLucerna::new);
        register(Constellations.bootes, CEffectBootes::new);
        register(Constellations.horologium, CEffectHorologium::new);
        register(Constellations.octans, CEffectOctans::new);
        register(Constellations.fornax, CEffectFornax::new);
        register(Constellations.pelotrio, CEffectPelotrio::new);

        MinecraftForge.EVENT_BUS.post(new APIRegistryEvent.ConstellationEffectRegister());
    }

    public static void addDynamicConfigEntries() {
        Config.addDynamicEntry(new CEffectAevitas(null));
        Config.addDynamicEntry(new CEffectDiscidia(null));
        Config.addDynamicEntry(new CEffectArmara(null));
        Config.addDynamicEntry(new CEffectVicio(null));
        Config.addDynamicEntry(new CEffectEvorsio(null));

        Config.addDynamicEntry(new CEffectHorologium(null));
        Config.addDynamicEntry(new CEffectMineralis(null));
        Config.addDynamicEntry(new CEffectLucerna(null));
        Config.addDynamicEntry(new CEffectBootes(null));
        Config.addDynamicEntry(new CEffectOctans(null));
        Config.addDynamicEntry(new CEffectFornax(null));
        Config.addDynamicEntry(new CEffectPelotrio(null));
    }

    private static void register(IWeakConstellation c, ConstellationEffectProvider provider) {
        providerMap.put(c, provider);
        singleRenderInstances.put(c, provider.provideEffectInstance(null));
    }

    public static void registerFromAPI(IWeakConstellation c, Function<ILocatable, ConstellationEffect> providerFunc) {
        providerMap.put(c, new APIConstellationProvider(providerFunc));
        singleRenderInstances.put(c, providerFunc.apply(null));
    }

    @Nullable
    public static ConstellationEffect clientRenderInstance(IWeakConstellation c) {
        return singleRenderInstances.get(c);
    }

    @Nullable
    public static ConstellationEffect getEffect(IWeakConstellation c, ILocatable origin) {
        ConstellationEffectProvider p = providerMap.get(c);
        if (p != null) {
            return p.provideEffectInstance(origin);
        }
        return null;
    }

    public static interface ConstellationEffectProvider {

        public ConstellationEffect provideEffectInstance(ILocatable origin);

    }

    public static class APIConstellationProvider implements ConstellationEffectProvider {

        private final Function<ILocatable, ConstellationEffect> providerFunc;

        public APIConstellationProvider(Function<ILocatable, ConstellationEffect> providerFunc) {
            this.providerFunc = providerFunc;
        }

        @Override
        public ConstellationEffect provideEffectInstance(ILocatable origin) {
            return providerFunc.apply(origin);
        }
    }

}
