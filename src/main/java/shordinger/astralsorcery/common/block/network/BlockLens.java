/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.block.BlockVariants;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.CrystalPropertyItem;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.Sounds;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.network.TileCrystalLens;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLens
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:31
 */
public class BlockLens extends BlockStarlightNetwork implements BlockVariants, CrystalPropertyItem {

    private static final AxisAlignedBB boxLensDown = new AxisAlignedBB(
        2.5D / 16D,
        0,
        2.5D / 16D,
        13.5D / 16D,
        14.5D / 16D,
        13.5D / 16D);
    private static final AxisAlignedBB boxLensUp = new AxisAlignedBB(
        2.5D / 16D,
        1.5D / 16D,
        2.5D / 16D,
        13.5D / 16D,
        1,
        13.5D / 16D);
    private static final AxisAlignedBB boxLensNorth = new AxisAlignedBB(
        2.5D / 16D,
        2.5D / 16D,
        0,
        13.5D / 16D,
        13.5D / 16D,
        14.5D / 16D);
    private static final AxisAlignedBB boxLensSouth = new AxisAlignedBB(
        2.5D / 16D,
        2.5D / 16D,
        1.5D / 16D,
        13.5D / 16D,
        13.5D / 16D,
        1);
    private static final AxisAlignedBB boxLensEast = new AxisAlignedBB(
        1.5D / 16D,
        2.5D / 16D,
        2.5D / 16D,
        1,
        13.5D / 16D,
        13.5D / 16D);
    private static final AxisAlignedBB boxLensWest = new AxisAlignedBB(
        0,
        2.5D / 16D,
        2.5D / 16D,
        14.5D / 16D,
        13.5D / 16D,
        13.5D / 16D);

    public static PropertyBool RENDER_FULLY = PropertyBool.create("render");
    public static PropertyEnum<EnumFacing> PLACED_AGAINST = PropertyEnum.create("against", EnumFacing.class);

    public BlockLens() {
        super(Material.ROCK, MapColor.BLACK);
        setHardness(3.0F);
        setSoundType(SoundType.GLASS);
        setResistance(12.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(
            this.blockState.getBaseState()
                .withProperty(RENDER_FULLY, true)
                .withProperty(PLACED_AGAINST, EnumFacing.DOWN));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        list.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, getMaxSize(stack));
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(PLACED_AGAINST, facing.getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.UP;
        for (EnumFacing f : EnumFacing.values()) {
            if (f.ordinal() == meta) {
                facing = f;
                break;
            }
        }
        return getDefaultState().withProperty(PLACED_AGAINST, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PLACED_AGAINST)
            .ordinal();
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(PLACED_AGAINST)) {
            case NORTH:
                return boxLensNorth;
            case SOUTH:
                return boxLensSouth;
            case WEST:
                return boxLensWest;
            case EAST:
                return boxLensEast;
            case UP:
                return boxLensUp;
            default:
            case DOWN:
                return boxLensDown;
        }
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrystalLens();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PLACED_AGAINST, RENDER_FULLY);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        ItemStack stack = super.getPickBlock(getActualState(state, world, pos), target, world, pos, player);
        TileCrystalLens lens = MiscUtils.getTileAt(world, pos, TileCrystalLens.class, true);
        if (lens != null && lens.getCrystalProperties() != null) {
            CrystalProperties.applyCrystalProperties(stack, lens.getCrystalProperties());
        } else {
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        }
        return stack;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
        if (lens != null && !worldIn.isRemote && !player.isCreative()) {
            ItemStack drop;
            if (lens.getLensColor() != null) {
                drop = lens.getLensColor()
                    .asStack();
                ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            }

            drop = new ItemStack(BlocksAS.lens);
            if (lens.getCrystalProperties() != null) {
                CrystalProperties.applyCrystalProperties(drop, lens.getCrystalProperties());
            } else {
                CrystalProperties.applyCrystalProperties(drop, new CrystalProperties(1, 0, 0, 0, -1));
            }
            ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
            if (lens != null && lens.getLensColor() != null) {
                ItemStack drop = lens.getLensColor()
                    .asStack();
                ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                SoundHelper.playSoundAround(Sounds.clipSwitch, worldIn, pos, 0.8F, 1.5F);
                lens.setLensColor(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(RENDER_FULLY, false);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        TileCrystalLens te = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
        if (te == null) return;
        te.onPlace(CrystalProperties.getCrystalProperties(stack));
    }

    @Override
    public List<IBlockState> getValidStates() {
        return Arrays.asList(
            getDefaultState().withProperty(RENDER_FULLY, false),
            getDefaultState().withProperty(RENDER_FULLY, true));
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(RENDER_FULLY)
            .toString();
    }
}
