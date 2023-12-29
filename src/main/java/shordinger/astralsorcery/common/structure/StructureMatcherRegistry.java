/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Maps;

import shordinger.astralsorcery.common.util.Provider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatcherRegistry
 * Created by HellFirePvP
 * Date: 02.12.2018 / 00:58
 */
public class StructureMatcherRegistry {

    public static final StructureMatcherRegistry INSTANCE = new StructureMatcherRegistry();

    private final Map<ResourceLocation, Provider<StructureMatcher>> matcherRegistry = Maps.newHashMap();

    private StructureMatcherRegistry() {
    }

    public void register(Provider<StructureMatcher> matchProvider) {
        StructureMatcher match = matchProvider.provide();
        this.matcherRegistry.put(match.getRegistryName(), matchProvider);
    }

    @Nullable
    public StructureMatcher provideNewMatcher(ResourceLocation key) {
        Provider<StructureMatcher> provider = this.matcherRegistry.get(key);
        return provider == null ? null : provider.provide();
    }

}
