/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import shordinger.astralsorcery.common.structure.change.BlockStateChangeSet;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatcher
 * Created by HellFirePvP
 * Date: 02.12.2018 / 01:02
 */
public abstract class StructureMatcher {

    private final ResourceLocation registryName;

    public StructureMatcher(@Nonnull ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public abstract ObservableArea getObservableArea();

    public abstract boolean notifyChange(IBlockAccess world, BlockPos centre, BlockStateChangeSet changeSet);

    @Nonnull
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public abstract void readFromNBT(NBTTagCompound tag);

    public abstract void writeToNBT(NBTTagCompound tag);

}
