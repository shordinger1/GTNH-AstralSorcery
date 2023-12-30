//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shordinger.astralsorcery.migration.block;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.migration.Mirror;
import shordinger.astralsorcery.migration.Rotation;

public interface IBlockState extends IBlockBehaviors, IBlockProperties {

    Collection<IProperty<?>> getPropertyKeys();

    <T extends Comparable<T>> T getValue(IProperty<T> var1);

    <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> var1, V var2);

    <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> var1);

    ImmutableMap<IProperty<?>, Comparable<?>> getProperties();

    Block getBlock();
}

interface IBlockBehaviors {

    boolean onBlockEventReceived(World var1, BlockPos var2, int var3, int var4);

    void neighborChanged(World var1, BlockPos var2, Block var3, BlockPos var4);
}

public interface IBlockProperties {

    Material getMaterial();

    boolean isFullBlock();

    boolean canEntitySpawn(Entity var1);

    /**
     * @deprecated
     */
    @Deprecated
    int getLightOpacity();

    int getLightOpacity(IBlockAccess var1, BlockPos var2);

    /**
     * @deprecated
     */
    @Deprecated
    int getLightValue();

    int getLightValue(IBlockAccess var1, BlockPos var2);

    @SideOnly(Side.CLIENT)
    boolean isTranslucent();

    boolean useNeighborBrightness();

    MapColor getMapColor(IBlockAccess var1, BlockPos var2);

    IBlockState withRotation(Rotation var1);

    IBlockState withMirror(Mirror var1);

    boolean isFullCube();

    @SideOnly(Side.CLIENT)
    boolean hasCustomBreakingProgress();

    EnumBlockRenderType getRenderType();

    @SideOnly(Side.CLIENT)
    int getPackedLightmapCoords(IBlockAccess var1, BlockPos var2);

    @SideOnly(Side.CLIENT)
    float getAmbientOcclusionLightValue();

    boolean isBlockNormalCube();

    boolean isNormalCube();

    boolean canProvidePower();

    int getWeakPower(IBlockAccess var1, BlockPos var2, ForgeDirection var3);

    boolean hasComparatorInputOverride();

    int getComparatorInputOverride(World var1, BlockPos var2);

    float getBlockHardness(World var1, BlockPos var2);

    float getPlayerRelativeBlockHardness(EntityPlayer var1, World var2, BlockPos var3);

    int getStrongPower(IBlockAccess var1, BlockPos var2, ForgeDirection var3);

    EnumPushReaction getMobilityFlag();

    IBlockState getActualState(IBlockAccess var1, BlockPos var2);

    @SideOnly(Side.CLIENT)
    AxisAlignedBB getSelectedBoundingBox(World var1, BlockPos var2);

    @SideOnly(Side.CLIENT)
    boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, ForgeDirection var3);

    boolean isOpaqueCube();

    @Nullable
    AxisAlignedBB getCollisionBoundingBox(IBlockAccess var1, BlockPos var2);

    void addCollisionBoxToList(World var1, BlockPos var2, AxisAlignedBB var3, List<AxisAlignedBB> var4,
                               @Nullable Entity var5, boolean var6);

    AxisAlignedBB getBoundingBox(IBlockAccess var1, BlockPos var2);

    RayTraceResult collisionRayTrace(World var1, BlockPos var2, BlockPos var3, BlockPos var4);

    /**
     * @deprecated
     */
    @Deprecated
    boolean isTopSolid();

    boolean doesSideBlockRendering(IBlockAccess var1, BlockPos var2, ForgeDirection var3);

    boolean isSideSolid(IBlockAccess var1, BlockPos var2, ForgeDirection var3);

    boolean doesSideBlockChestOpening(IBlockAccess var1, BlockPos var2, ForgeDirection var3);

    Vec3 getOffset(IBlockAccess var1, BlockPos var2);

    boolean causesSuffocation();

    BlockFaceShape getBlockFaceShape(IBlockAccess var1, BlockPos var2, ForgeDirection var3);
}



