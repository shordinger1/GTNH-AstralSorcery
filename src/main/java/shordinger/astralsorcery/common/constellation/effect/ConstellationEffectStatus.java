/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectStatus
 * Created by HellFirePvP
 * Date: 14.01.2018 / 21:23
 */
// Interface for constellation effects that don't care how strong the starlight influx is, they just provide a certain
// status
public interface ConstellationEffectStatus {

    public abstract boolean runEffect(World world, BlockPos pos, int mirrorAmount,
                                      ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect);

}
