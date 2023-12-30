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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.block.network.BlockStarlightNetwork;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.BlockStructureObserver;
import shordinger.astralsorcery.common.tile.TileStarlightInfuser;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.block.BlockFaceShape;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.EnumBlockRenderType;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:05
 */
public class BlockStarlightInfuser extends BlockStarlightNetwork implements BlockStructureObserver {

    private static final AxisAlignedBB box = new AxisAlignedBB(0D, 0D, 0D, 1D, 12D / 16D, 1D);

    public BlockStarlightInfuser() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(1.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                     ForgeDirection side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote ) {
            TileStarlightInfuser infuser = MiscUtils.getTileAt(worldIn, pos, TileStarlightInfuser.class, true);
            if (infuser != null) {
                infuser.onInteract(playerIn, playerIn.getHeldItem());
            }
        }
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileStarlightInfuser infuser = MiscUtils.getTileAt(worldIn, pos, TileStarlightInfuser.class, true);
            if (infuser != null && !infuser.getInputStack()
                .isEmpty()) {
                ItemUtils.dropItemNaturally(
                    worldIn,
                    infuser.getPos()
                        .getX() + 0.5,
                    infuser.getPos()
                        .getY() + 1,
                    infuser.getPos()
                        .getZ() + 0.5,
                    infuser.getInputStack());
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStarlightInfuser();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            ForgeDirection p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStarlightInfuser();
    }
}
