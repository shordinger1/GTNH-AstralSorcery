/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCustomFlower
 * Created by HellFirePvP
 * Date: 28.03.2017 / 23:24
 */
public class BlockCustomFlower extends Block implements BlockCustomName, BlockVariants, IShearable {

    public static final PropertyEnum<FlowerType> FLOWER_TYPE = PropertyEnum.create("flower", FlowerType.class);
    private static final AxisAlignedBB box = new AxisAlignedBB(
        1.5D / 16D,
        0,
        1.5D / 16D,
        14.5D / 16D,
        13D / 16D,
        14.5D / 16D);
    private static final Random rand = new Random();

    public BlockCustomFlower() {
        super(Material.PLANTS);
        setLightLevel(0.2F);
        setSoundType(SoundType.PLANT);
        setTickRandomly(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
        if (!(world instanceof World)) {
            return;
        }
        switch (state.getValue(FLOWER_TYPE)) {
            case GLOW_FLOWER:
                int size = 1;
                for (int i = 0; i < fortune; i++) {
                    size += rand.nextInt(3) + 1;
                }
                for (int i = 0; i < size; i++) {
                    ItemUtils.dropItemNaturally(
                        (World) world,
                        pos.getX() + 0.5,
                        pos.getY() + 0.1,
                        pos.getZ() + 0.5,
                        new ItemStack(Items.GLOWSTONE_DUST));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canBlockStay(worldIn, pos);
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.isSideSolid(worldIn, pos, EnumFacing.UP);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FLOWER_TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FLOWER_TYPE, FlowerType.values()[meta]);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FLOWER_TYPE)
            .getMeta();
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        return getStateFromMeta(meta).getValue(FLOWER_TYPE)
            .getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> states = new LinkedList<>();
        for (FlowerType type : FlowerType.values()) {
            states.add(getDefaultState().withProperty(FLOWER_TYPE, type));
        }
        return states;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(FLOWER_TYPE)
            .getName();
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Lists.newArrayList(ItemUtils.createBlockStack(WorldHelper.getBlockState(world, pos)));
    }

    public static enum FlowerType implements IStringSerializable {

        GLOW_FLOWER;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public int getMeta() {
            return ordinal();
        }

    }

}
