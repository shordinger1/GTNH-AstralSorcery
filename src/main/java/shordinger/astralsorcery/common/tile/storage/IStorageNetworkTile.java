/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.storage;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.auxiliary.StorageNetworkHandler;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStorageNetworkTile
 * Created by HellFirePvP
 * Date: 13.12.2017 / 20:23
 */
// TileEntity interface!
public interface IStorageNetworkTile<T extends IStorageNetworkTile<T>> extends ILocatable {

    // Should return the actual network core this tileentity is associated with.
    // May chain onto other cores that then resolve their owner with this.
    public T getAssociatedCore();

    // The world the network is in. Usually the tile's world
    public World getNetworkWorld();

    // This tile's notification of mapping or network changes
    public void receiveMappingChange(StorageNetworkHandler.MappingChange newMapping);

    @Nullable
    default public StorageNetwork getNetwork() {
        return StorageNetworkHandler.getHandler(getNetworkWorld())
            .getNetwork(getAssociatedCore().getLocationPos());

    }

    // TODO change/redo and get data from network instead.
    // Can be adjusted to do a different lookup logic.
    default public T resolveMasterCore() {
        T assoc = getAssociatedCore();
        T next;
        while (assoc != (next = getAssociatedCore())) {
            assoc = next;
        }
        return assoc;
    }

}
