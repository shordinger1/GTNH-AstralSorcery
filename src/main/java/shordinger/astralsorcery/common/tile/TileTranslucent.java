/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.common.tile.base.TileEntitySynchronized;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTranslucent
 * Created by HellFirePvP
 * Date: 17.01.2017 / 03:45
 */
public class TileTranslucent extends TileEntitySynchronized {

    private IBlockState fakedState = Blocks.air.getDefaultState();

    public IBlockState getFakedState() {
        return fakedState;
    }

    public void setFakedState(IBlockState fakedState) {
        this.fakedState = fakedState;
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("Block") && compound.hasKey("Data")) {
            int data = compound.getInteger("Data");
            Block b = Block.getBlockFromName(compound.getString("Block"));
            if (b != null) {
                fakedState = b.getStateFromMeta(data);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (fakedState != null) {
            compound.setString(
                "Block",
                Block.REGISTRY.getNameForObject(fakedState.getBlock())
                    .toString());
            compound.setInteger(
                "Data",
                fakedState.getBlock()
                    .getMetaFromState(fakedState));
        }
    }

}
