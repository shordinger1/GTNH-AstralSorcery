/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileStructuralConnector;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockContainer;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.PropertyEnum;
import shordinger.wrapper.net.minecraft.block.state.BlockFaceShape;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.particle.ParticleManager;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumBlockRenderType;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.IStringSerializable;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.RayTraceResult;
import shordinger.wrapper.net.minecraft.world.Explosion;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructural
 * Created by HellFirePvP
 * Date: 30.07.2016 / 21:50
 */
public class BlockStructural extends BlockContainer implements BlockCustomName, BlockVariants {

    private static boolean effectLoop = false;
    public static PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.create("blocktype", BlockType.class);

    public BlockStructural() {
        super(Material.BARRIER, MapColor.AIR);
        setBlockUnbreakable();
        setSoundType(SoundType.GLASS);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                IBlockState downState = world.getBlockState(pos.down());
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getBlock()
                    .getSoundType(downState, world, pos, entity);
            default:
                break;
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        return state.getValue(BLOCK_TYPE)
            .getSupportedState()
            .getBlock()
            .getHarvestTool(
                state.getValue(BLOCK_TYPE)
                    .getSupportedState());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        if (effectLoop) return false;
        effectLoop = true;
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                Minecraft.getMinecraft().effectRenderer
                    .addBlockDestroyEffects(pos.down(), world.getBlockState(pos.down()));
                effectLoop = false;
                return true;
            default:
                break;
        }
        effectLoop = false;
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return new AxisAlignedBB(0, -1, 0, 1, 1, 1);
            default:
                break;
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getBlock()
                    .onBlockActivated(
                        worldIn,
                        pos.down(),
                        BlockType.TELESCOPE_STRUCT.getSupportedState(),
                        playerIn,
                        hand,
                        facing,
                        hitX,
                        hitY,
                        hitZ);
            default:
                break;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                drops.add(BlockMachine.MachineType.TELESCOPE.asStack());
                break;
            default:
                break;
        }
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        switch (blockState.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getBlockHardness(worldIn, pos.down());
            default:
                break;
        }
        return super.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getBlock()
                    .getExplosionResistance(world, pos.down(), exploder, explosion);
            default:
                break;
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .getBlock()
                    .getPickBlock(BlockType.TELESCOPE_STRUCT.getSupportedState(), target, world, pos.down(), player);
            default:
                break;
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                if (world.isAirBlock(pos.down())) {
                    world.setBlockToAir(pos);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if (!(world instanceof World)) {
            super.onNeighborChange(world, pos, neighbor);
            return;
        }
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                if (world.isAirBlock(pos.down())) {
                    ((World) world).setBlockToAir(pos);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < BlockType.values().length ? getDefaultState().withProperty(BLOCK_TYPE, BlockType.values()[meta])
            : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BlockType type = state.getValue(BLOCK_TYPE);
        return type.ordinal();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_TYPE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return state.getValue(BLOCK_TYPE)
            .getSupportedState()
            .isOpaqueCube();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(BLOCK_TYPE)
            .getSupportedState()
            .isFullCube();
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .isNormalCube();
            default:
                break;
        }
        return super.isNormalCube(state);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        switch (base_state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState()
                    .isSideSolid(world, pos.down(), side);
            default:
                break;
        }
        return super.isSideSolid(base_state, world, pos, side);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlockType mt = getStateFromMeta(meta).getValue(BLOCK_TYPE);
        return mt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> li = new ArrayList<>(BlockType.values().length);
        for (BlockType bt : BlockType.values()) li.add(getDefaultState().withProperty(BLOCK_TYPE, bt));
        return li;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(BLOCK_TYPE)
            .getName();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStructuralConnector();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStructuralConnector();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public static enum BlockType implements IStringSerializable {

        TELESCOPE_STRUCT(BlocksAS.blockMachine.getDefaultState()
            .withProperty(BlockMachine.MACHINE_TYPE, BlockMachine.MachineType.TELESCOPE));

        private final IBlockState supportedState;

        private BlockType(IBlockState supportedState) {
            this.supportedState = supportedState;
        }

        public IBlockState getSupportedState() {
            return supportedState;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
