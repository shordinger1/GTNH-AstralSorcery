/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.block.network.BlockStarlightNetwork;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.tile.TileTreeBeacon;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTreeBeacon
 * Created by HellFirePvP
 * Date: 30.12.2016 / 13:26
 */
public class BlockTreeBeacon extends BlockStarlightNetwork implements BlockDynamicStateMapper.Festive, BlockVariants {

    private static final AxisAlignedBB box = new AxisAlignedBB(3D / 16D, 0D, 3D / 16D, 13D / 16D, 1D, 13D / 16D);

    public BlockTreeBeacon() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(1.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.PLANT);
        setLightLevel(0.7F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public Map<IBlockState, ModelResourceLocation> getModelLocations(Block blockIn) {
        Map<IBlockState, ModelResourceLocation> out = Maps.newHashMap();
        ResourceLocation rl = Block.REGISTRY.getNameForObject(blockIn);
        rl = new ResourceLocation(rl.getResourceDomain(), rl.getResourcePath() + "_festive");
        out.put(
            blockIn.getDefaultState(),
            new ModelResourceLocation(
                rl,
                getPropertyString(
                    blockIn.getDefaultState()
                        .getProperties())));
        return out;
    }

    @Override
    public String getStateName(IBlockState state) {
        if (handleRegisterStateMapper()) {
            return "festive";
        }
        return "normal";
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
                                ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        TileTreeBeacon ttb = MiscUtils.getTileAt(worldIn, pos, TileTreeBeacon.class, true);
        if (ttb != null && !worldIn.isRemote
            && placer instanceof EntityPlayerMP
            && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) placer)) {
            ttb.setPlacedBy((EntityPlayer) placer);
        }
    }

    @Override
    public List<IBlockState> getValidStates() {
        return Lists.newArrayList(getDefaultState());
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
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
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileTreeBeacon();
    }

}
