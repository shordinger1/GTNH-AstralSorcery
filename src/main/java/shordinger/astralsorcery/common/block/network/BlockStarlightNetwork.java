/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import com.gtnewhorizons.modularui.api.forge.IItemHandler;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.tile.base.TileNetwork;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.block.AstralBlockContainer;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 21:01
 */
public abstract class BlockStarlightNetwork extends AstralBlockContainer {

    public BlockStarlightNetwork(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn);
    }

    public BlockStarlightNetwork(Material materialIn) {
        super(materialIn);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileNetwork teN = MiscUtils.getTileAt(worldIn, pos, TileNetwork.class, true);
        if (teN != null) {
            teN.onBreak();
        }

        TileEntity inv = MiscUtils.getTileAt(worldIn, pos, TileEntity.class, true);
        if (inv != null && !worldIn.isRemote) {
            for (ForgeDirection face : ForgeDirection.values()) {
                IItemHandler handle = inv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
                if (handle != null) {
                    ItemUtils.dropInventory(handle, worldIn, pos);
                    break;
                }
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

}
