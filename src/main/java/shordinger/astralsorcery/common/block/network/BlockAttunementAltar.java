/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.block.BlockMarble;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.structure.BlockStructureObserver;
import shordinger.astralsorcery.common.tile.TileAttunementAltar;
import shordinger.wrapper.net.minecraft.block.BlockContainer;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.particle.ParticleManager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementAltar
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:20
 */
public class BlockAttunementAltar extends BlockContainer implements BlockStructureObserver {

    public static final AxisAlignedBB boxAttunementAlar = new AxisAlignedBB(
        -2D / 16D,
        0,
        -2D / 16D,
        18D / 16D,
        6D / 16D,
        18D / 16D);

    public BlockAttunementAltar() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setLightLevel(0.8F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        RenderingUtils.playBlockBreakParticles(
            pos,
            BlocksAS.blockMarble.getDefaultState()
                .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW));
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boxAttunementAlar;
    }

    /*
     * @Override
     * public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack
     * stack) {
     * //worldIn.setBlockState(pos.up(),
     * BlocksAS.blockStructural.getDefaultState().withProperty(BlockStructural.BLOCK_TYPE,
     * BlockStructural.BlockType.ATTUNEMENT_ALTAR_STRUCT));
     * TileAttunementAltar te = MiscUtils.getTileAt(worldIn, pos, TileAttunementAltar.class, true);
     * if(te != null && !worldIn.isRemote) {
     * if(placer != null && placer instanceof EntityPlayer) {
     * te.setOwner(placer.getUniqueID());
     * }
     * }
     * super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
     * }
     */

    /*
     * @Override
     * public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbor) {
     * if(world.isAirBlock(pos.up())) {
     * world.setBlockToAir(pos);
     * }
     * super.neighborChanged(state, world, pos, neighbor);
     * }
     * @Override
     * public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
     * if(!(world instanceof World)) {
     * super.onNeighborChange(world, pos, neighbor);
     * return;
     * }
     * if(world.isAirBlock(pos.up())) {
     * ((World) world).setBlockToAir(pos);
     * }
     * }
     */

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return p_193383_4_ == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        return super.getPickBlock(world.getBlockState(pos), target, world, pos, player);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAttunementAltar();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAttunementAltar();
    }

}
