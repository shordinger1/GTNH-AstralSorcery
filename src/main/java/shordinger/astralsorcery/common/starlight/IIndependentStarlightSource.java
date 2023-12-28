/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IIndependentStarlightSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 12:34
 */
public interface IIndependentStarlightSource {

    // As the purpose of the source, this should produce the starlight - called once every tick
    public float produceStarlightTick(World world, BlockPos pos);

    public IWeakConstellation getStarlightType();

    default public boolean providesAutoLink() {
        return false;
    }

    // Update the state of the independent tile. for example if "doesSeeSky" has changed or something.
    public void informTileStateChange(IStarlightSource sourceTile);

    // Update (maybe) if proximity to other sources should be checked - to prevent the user from placing everything
    // super dense.
    // Threaded to prevent overhead, so remember to sync savely to avoid CME or other threaded stuffs.
    // You may only do position-based logic here. Data on the sources MIGHT be invalid at this early stage of changes.
    // Called whenever sources are changed (added/removed) from a world.
    public void threadedUpdateProximity(BlockPos thisPos, Map<BlockPos, IIndependentStarlightSource> otherSources);

    public SourceClassRegistry.SourceProvider getProvider();

    public void readFromNBT(NBTTagCompound compound);

    public void writeToNBT(NBTTagCompound compound);

}
