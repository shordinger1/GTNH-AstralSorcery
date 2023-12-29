/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.migration.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEntityCollect
 * Created by HellFirePvP
 * Date: 09.11.2016 / 18:42
 */
public abstract class CEffectEntityCollect<T extends Entity> extends ConstellationEffect {

    private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    protected final Class<T> classToSearch;
    protected final Predicate<T> searchFilter;
    protected double range;
    public boolean enabled = true;

    public CEffectEntityCollect(@Nullable ILocatable origin, IWeakConstellation constellation, String cfgName,
                                double defaultRange, Class<T> entityClass, Predicate<T> filter) {
        super(origin, constellation, cfgName);
        this.classToSearch = entityClass;
        this.searchFilter = filter;
        this.range = defaultRange;
    }

    public List<T> collectEntities(World world, BlockPos pos, ConstellationEffectProperties prop) {
        if (!enabled) return Lists.newArrayList();
        return world.getEntitiesWithinAABB(
            classToSearch,
            BOX.grow(prop.getSize())
                .offset(pos),
            searchFilter);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        range = cfg.getFloat(
            getKey() + "Range",
            getConfigurationSection(),
            (float) range,
            2,
            64,
            "Defines the range in which the ritual will try to find entities");
        enabled = cfg.getBoolean(
            getKey() + "Enabled",
            getConfigurationSection(),
            true,
            "Set to false to disable this ConstellationEffect.");
    }

}
