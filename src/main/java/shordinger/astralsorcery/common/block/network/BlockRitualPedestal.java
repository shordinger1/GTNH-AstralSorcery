/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.TileRitualPedestal;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:45
 */
public class BlockRitualPedestal extends BlockStarlightNetwork {

    private static final AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 1, 13D / 16D, 1);

    public BlockRitualPedestal() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        ItemStack pedestal = new ItemStack(this);
        list.add(pedestal);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileRitualPedestal();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileRitualPedestal ped = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if (ped != null && !worldIn.isRemote) {
            ItemUtils.dropItem(
                worldIn,
                pos.getX() + 0.5,
                pos.getY() + 0.8,
                pos.getZ() + 0.5,
                ItemUtils.copyStackWithSize(
                    ped.getCatalystCache(),
                    ped.getCatalystCache()
                        .getCount()));
        }

        super.breakBlock(worldIn, pos, state);
    }

    /*
     * @Override
     * @SideOnly(Side.CLIENT)
     * public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
     * RenderingUtils.playBlockBreakParticles(pos,
     * BlocksAS.blockMarble.getDefaultState()
     * .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW));
     * return true;
     * }
     * @Override
     * @SideOnly(Side.CLIENT)
     * public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
     * return true;
     * }
     */

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileRitualPedestal pedestal = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if (pedestal == null) {
            return false;
        }
        if (worldIn.isRemote) {
            return true;
        }
        ItemStack heldItem = playerIn.getHeldItem(hand);

        ItemStack in = pedestal.getCurrentPedestalCrystal();
        if (!heldItem.isEmpty() && in.isEmpty() && ItemTunedCrystalBase.getMainConstellation(heldItem) != null) {
            playerIn.setHeldItem(hand, pedestal.placeCrystalIntoPedestal(heldItem));
            return true;
        }
        if (!in.isEmpty() && playerIn.isSneaking()) {
            pedestal.placeCrystalIntoPedestal(null);
            playerIn.inventory.placeItemBackInInventory(worldIn, in);
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileRitualPedestal te = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if (te != null && !worldIn.isRemote) {
            BlockPos toCheck = pos.up();
            IBlockState other = worldIn.getBlockState(toCheck);
            if (other.isSideSolid(worldIn, toCheck, EnumFacing.DOWN)) {
                ItemUtils.dropItem(
                    worldIn,
                    pos.getX() + 0.5,
                    pos.getY() + 0.8,
                    pos.getZ() + 0.5,
                    ItemUtils.copyStackWithSize(
                        te.getCatalystCache(),
                        te.getCatalystCache()
                            .getCount()));
                te.placeCrystalIntoPedestal(null);
                te.markForUpdate();
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        TileRitualPedestal te = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if (te != null && !worldIn.isRemote) {
            if (placer instanceof EntityPlayer) {
                te.setOwner(placer.getUniqueID());
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}
