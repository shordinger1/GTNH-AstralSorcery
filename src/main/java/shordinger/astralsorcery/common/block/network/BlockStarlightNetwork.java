/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import shordinger.astralsorcery.common.tile.base.TileNetwork;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.BlockContainer;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.items.CapabilityItemHandler;
import shordinger.wrapper.net.minecraftforge.items.IItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 21:01
 */
public abstract class BlockStarlightNetwork extends BlockContainer {

    public BlockStarlightNetwork(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
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
            for (EnumFacing face : EnumFacing.VALUES) {
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
