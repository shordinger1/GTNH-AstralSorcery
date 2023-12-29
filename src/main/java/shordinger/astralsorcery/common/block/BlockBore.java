/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.BlockStructureObserver;
import shordinger.astralsorcery.common.tile.TileBore;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBore
 * Created by HellFirePvP
 * Date: 03.11.2017 / 14:49
 */
public class BlockBore extends BlockContainer implements BlockStructureObserver {

    public BlockBore() {
        super(Material.WOOD, MapColor.GOLD);
        setHarvestLevel("axe", 2);
        setHardness(3.0F);
        setSoundType(SoundType.WOOD);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos.down())
            .getBlock()
            .isReplaceable(worldIn, pos.down())) {
            TileBore tb = MiscUtils.getTileAt(worldIn, pos, TileBore.class, true);
            ItemStack held = playerIn.getHeldItem(hand);
            if (tb != null && !held.isEmpty()
                && held.getItem() instanceof ItemBlock
                && ((ItemBlock) held.getItem()).getBlock() instanceof BlockBoreHead) {
                if (!worldIn.isRemote) {
                    if (worldIn
                        .setBlockState(pos.down(), BlocksAS.blockBoreHead.getStateFromMeta(held.getItemDamage()))) {
                        if (!playerIn.isCreative()) {
                            held.shrink(1);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBore();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

}
