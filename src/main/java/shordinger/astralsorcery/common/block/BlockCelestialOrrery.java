/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import shordinger.astralsorcery.common.block.network.BlockStarlightNetwork;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.BlockStructureObserver;
import shordinger.astralsorcery.common.tile.TileCelestialOrrery;
import shordinger.astralsorcery.migration.block.BlockFaceShape;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialOrrery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 22:42
 */
public class BlockCelestialOrrery extends BlockStarlightNetwork implements BlockStructureObserver {

    public BlockCelestialOrrery() {
        super(Material.rock, MapColor.quartzColor);
        setHardness(0.5F);
        setHarvestLevel("axe", 1);
        setResistance(10F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCelestialOrrery();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCelestialOrrery();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            ForgeDirection p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

}
