/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import shordinger.wrapper.net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureRegistry
 * Created by HellFirePvP
 * Date: 02.12.2018 / 10:16
 */
public class StructureRegistry {

    public static final StructureRegistry INSTANCE = new StructureRegistry();

    private Map<ResourceLocation, MatchableStructure> matcherRegistry = Maps.newHashMap();

    private StructureRegistry() {}

    public void register(MatchableStructure structure) {
        this.matcherRegistry.put(structure.getRegistryName(), structure);
    }

    @Nullable
    public MatchableStructure getStructure(ResourceLocation key) {
        return this.matcherRegistry.get(key);
    }

}
