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
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.base.RockCrystalHandler;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.material.MapColor;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.properties.PropertyEnum;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.BlockRenderLayer;
import shordinger.wrapper.net.minecraft.util.IStringSerializable;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCrystalOre
 * Created by HellFirePvP
 * Date: 07.05.2016 / 18:03
 */
public class BlockCustomOre extends Block implements BlockCustomName, BlockVariants {

    public static boolean allowCrystalHarvest = false;
    private static final Random rand = new Random();

    public static PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("oretype", OreType.class);

    public BlockCustomOre() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);

        if (state.getValue(ORE_TYPE)
            .equals(OreType.ROCK_CRYSTAL)) {
            RockCrystalHandler.INSTANCE.removeOre(worldIn, pos, true);
        }
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (OreType t : OreType.values()) {
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        OreType type = state.getValue(ORE_TYPE);
        return type.getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < OreType.values().length ? getDefaultState().withProperty(ORE_TYPE, OreType.values()[meta])
            : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORE_TYPE);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, @Nullable ItemStack stack) {
        OreType type = state.getValue(ORE_TYPE);
        if (type != OreType.ROCK_CRYSTAL
            || (allowCrystalHarvest || (securityCheck(worldIn, player) && checkSafety(worldIn, pos)))) {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
        OreType type = state.getValue(ORE_TYPE);
        switch (type) {
            case ROCK_CRYSTAL:
                if (world != null && world instanceof World
                    && (allowCrystalHarvest
                    || (checkSafety((World) world, pos) && securityCheck((World) world, harvesters.get())))) {
                    drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                    for (int i = 0; i < (fortune + 1); i++) {
                        if (((World) world).rand.nextBoolean()) {
                            drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                        }
                    }
                    if (((World) world).rand.nextBoolean()) {
                        drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                    }
                }
                break;
            case STARMETAL:
                drops.add(new ItemStack(this, 1, OreType.STARMETAL.ordinal()));
                break;
            default:
                break;
        }
    }

    private boolean securityCheck(World world, EntityPlayer player) {
        return !world.isRemote && player != null && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player);
    }

    private boolean checkSafety(World world, BlockPos pos) {
        EntityPlayer player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
        return player != null && player.getDistanceSq(pos) < 100;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        OreType ot = getStateFromMeta(meta).getValue(ORE_TYPE);
        return ot.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        return singleEnumPropertyStates(getDefaultState(), ORE_TYPE, OreType.values());
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        OreType ot = state.getValue(ORE_TYPE);
        if (ot == OreType.ROCK_CRYSTAL) {
            if (Config.rockCrystalOreSilkTouchHarvestable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(ORE_TYPE)
            .getName();
    }

    @SideOnly(Side.CLIENT)
    public static void playStarmetalOreEffects(PktParticleEvent event) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
            event.getVec()
                .getX() + rand.nextFloat(),
            event.getVec()
                .getY() + rand.nextFloat(),
            event.getVec()
                .getZ() + rand.nextFloat());
        p.motion(0, rand.nextFloat() * 0.05, 0);
        p.scale(0.2F);
    }

    public static enum OreType implements IStringSerializable {

        ROCK_CRYSTAL(0),
        STARMETAL(1);

        private final int meta;

        private OreType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.customOre, 1, meta);
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
