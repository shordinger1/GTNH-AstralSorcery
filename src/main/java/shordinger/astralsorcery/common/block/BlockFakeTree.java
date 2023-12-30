/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import shordinger.astralsorcery.migration.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import shordinger.astralsorcery.common.tile.TileFakeTree;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.migration.block.AstralBlockContainer;
import shordinger.astralsorcery.migration.block.BlockFaceShape;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.BlockRenderLayer;
import shordinger.astralsorcery.migration.block.EnumBlockRenderType;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 20:31
 */
public class BlockFakeTree extends AstralBlockContainer {

    public BlockFakeTree() {
        super(Material.BARRIER);
        setBlockUnbreakable();
        setResistance(6000001.0F);
        setLightLevel(0.6F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        TileFakeTree tft = MiscUtils.getTileAt(world, pos, TileFakeTree.class, false);
        if (tft != null && tft.getFakedState() != null) {
            RenderingUtils.playBlockBreakParticles(pos, tft.getFakedState());
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileFakeTree tft = MiscUtils.getTileAt(worldIn, pos, TileFakeTree.class, false);
        if (tft == null || tft.getReference() == null) return;
        if (rand.nextInt(20) == 0) {
            Color c = new Color(63, 255, 63);
            PatreonEffectHelper.PatreonEffect pe;
            if (tft.getPlayerEffectRef() != null
                && (pe = PatreonEffectHelper.getPatreonEffects(Side.CLIENT, tft.getPlayerEffectRef())
                .stream()
                .filter(p -> p instanceof PtEffectTreeBeacon)
                .findFirst()
                .orElse(null)) != null) {
                c = new Color(((PtEffectTreeBeacon) pe).getColorTreeDrainEffects());
            }

            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX() + rand.nextFloat(),
                pos.getY() + rand.nextFloat(),
                pos.getZ() + rand.nextFloat());
            p.motion(0, 0, 0);
            p.scale(0.45F)
                .setColor(c)
                .setMaxAge(65);
        }
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        TileFakeTree tft = MiscUtils.getTileAt(world, pos, TileFakeTree.class, true);
        if (tft != null && tft.getFakedState() != null) {
            IBlockState fake = tft.getFakedState();
            return fake.getBlock()
                .getSoundType(fake, world, pos, entity);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
                         int fortune) {
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
                                            ForgeDirection p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, ForgeDirection side) {
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
        return new TileFakeTree();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
                                  EntityPlayer player) {
        TileFakeTree tft = MiscUtils.getTileAt(world, pos, TileFakeTree.class, true);
        try {
            if (tft != null && tft.getFakedState() != null) {
                return tft.getFakedState()
                    .getBlock()
                    .getPickBlock(tft.getFakedState(), target, world, pos, player);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

}
