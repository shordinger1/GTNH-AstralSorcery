/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import shordinger.astralsorcery.common.base.Mods;
import shordinger.astralsorcery.common.util.data.NonDuplicateArrayList;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.IProperty;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MiscUtils
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:38
 */
public class MiscUtils {

    public static final String GAMERULE_SKIP_SKYLIGHT_CHECK = "astralSorceryIgnoreSkyCheck";
    private static final Map<EnumDyeColor, Color> prettierColorMapping = new HashMap<>();

    @Nullable
    public static <T> T getTileAt(IBlockAccess world, BlockPos pos, Class<T> tileClass, boolean forceChunkLoad) {
        if (world == null || pos == null) return null; // Duh.
        if (world instanceof World) {
            if (!((World) world).isBlockLoaded(pos) && !forceChunkLoad) return null;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te == null) return null;
        if (tileClass.isInstance(te)) return (T) te;
        return null;
    }

    public static boolean canEntityTickAt(World world, BlockPos pos) {
        if (!isChunkLoaded(world, pos)) {
            return false;
        }
        BlockPos test = new BlockPos(pos.getX(), 0, pos.getZ());
        boolean isForced = world.getPersistentChunks()
            .containsKey(new ChunkPos(test));
        int range = isForced ? 0 : 32;
        return world.isAreaLoaded(test.add(-range, 0, -range), test.add(range, 0, range), true);
    }

    @Nullable
    public static <T> T getRandomEntry(List<T> list, Random rand) {
        if (list == null || list.isEmpty()) return null;
        return list.get(rand.nextInt(list.size()));
    }

    @Nullable
    public static <T> T getWeightedRandomEntry(Collection<T> list, Random rand,
                                               Function<T, Integer> getWeightFunction) {
        List<WRItemObject<T>> weightedItems = new ArrayList<>(list.size());
        for (T e : list) {
            weightedItems.add(new WRItemObject<>(getWeightFunction.apply(e), e));
        }
        WRItemObject<T> item = WeightedRandom.getRandomItem(rand, weightedItems);
        return item != null ? item.getValue() : null;
    }

    public static <T, V extends Comparable<V>> V getMaxEntry(Collection<T> elements, Function<T, V> valueFunction) {
        V max = null;
        for (T element : elements) {
            V val = valueFunction.apply(element);
            if (max == null || max.compareTo(val) < 0) {
                max = val;
            }
        }
        return max;
    }

    public static boolean canSeeSky(World world, BlockPos at, boolean loadChunk, boolean defaultValue) {
        if (world.getGameRules()
            .getGameRuleBooleanValue(GAMERULE_SKIP_SKYLIGHT_CHECK)) {
            return true;
        }

        if (!isChunkLoaded(world, at) && !loadChunk) {
            return defaultValue;
        }
        return world.canSeeSky(at);
    }

    public static <K, V, N> Map<K, N> remap(Map<K, V> map, Function<V, N> remapFct) {
        return map.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, (e) -> remapFct.apply(e.getValue())));
    }

    public static <T, K, V> List<T> flatten(Map<K, V> map, BiFunction<K, V, T> flatFunction) {
        return map.entrySet()
            .stream()
            .map((entry) -> flatFunction.apply(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public static <T> List<T> flatList(Collection<List<T>> listCollection) {
        return listCollection.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public static <T> List<T> flatNonDuplicateList(Collection<List<T>> listCollection) {
        return listCollection.stream()
            .flatMap(Collection::stream)
            .collect(mergeNonDuplicateList());
    }

    public static <K, V, L> Map<K, V> splitMap(Collection<L> col, Function<L, Tuple<K, V>> split) {
        Map<K, V> map = new HashMap<>();
        col.forEach(l -> {
            Tuple<K, V> result = split.apply(l);
            map.put(result.key, result.value);
        });
        return map;
    }

    public static <T> void mergeList(Collection<T> src, List<T> dst) {
        for (T element : src) {
            if (!dst.contains(element)) {
                dst.add(element);
            }
        }
    }

    public static <T> void cutList(List<T> toRemove, List<T> from) {
        for (T element : toRemove) {
            from.remove(element);
        }
    }

    @Nullable
    public static <T> T iterativeSearch(Collection<T> collection, Predicate<T> matchingFct) {
        for (T element : collection) {
            if (matchingFct.test(element)) {
                return element;
            }
        }
        return null;
    }

    public static <T> boolean contains(Collection<T> collection, Predicate<T> matchingFct) {
        return iterativeSearch(collection, matchingFct) != null;
    }

    @Nullable
    public static IBlockState getMatchingState(Collection<IBlockState> applicableStates, @Nullable IBlockState test) {
        for (IBlockState state : applicableStates) {
            if (matchStateExact(state, test)) {
                return state;
            }
        }
        return null;
    }

    public static <T> boolean matchesAny(T element, Collection<Predicate<T>> tests) {
        for (Predicate<T> test : tests) {
            if (test.test(element)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchStateExact(@Nullable IBlockState state, @Nullable IBlockState stateToTest) {
        if (state == null) {
            return stateToTest == null;
        } else if (stateToTest == null) {
            return false;
        }

        if (!state.getBlock()
            .getLocalizedName()
            .equals(
                stateToTest.getBlock()
                    .getLocalizedName())) {
            return false;
        }

        for (IProperty<?> prop : state.getPropertyKeys()) {
            Comparable<?> original = state.getValue(prop);
            try {
                Comparable<?> test = stateToTest.getValue(prop);
                if (!original.equals(test)) {
                    return false;
                }
            } catch (Exception exc) {
                return false;
            }
        }
        return true;
    }

    public static boolean canPlayerAttackServer(@Nullable EntityLivingBase source, @Nonnull EntityLivingBase target) {
        if (target.isDead) {
            return false;
        }
        if (target instanceof EntityPlayer) {
            EntityPlayer plTarget = (EntityPlayer) target;
            if (target.getEntityWorld() instanceof WorldServer && target.getEntityWorld()
                .getMinecraftServer() != null
                && target.getEntityWorld()
                .getMinecraftServer()
                .isPVPEnabled()) {
                return false;
            }
            if (plTarget.isSpectator() || plTarget.isCreative()) {
                return false;
            }
            return !(source instanceof EntityPlayer) || ((EntityPlayer) source).canAttackPlayer(plTarget);
        }
        return true;
    }

    public static boolean isFluidBlock(IBlockState state) {
        return state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase;
    }

    @Nullable
    public static Fluid tryGetFuild(IBlockState state) {
        if (!isFluidBlock(state)) {
            return null;
        }
        if (state.getBlock() instanceof BlockLiquid) {
            Material mat = state.getMaterial();
            if (mat == Material.water) {
                return FluidRegistry.WATER;
            } else if (mat == Material.lava) {
                return FluidRegistry.LAVA;
            }
        } else if (state.getBlock() instanceof BlockFluidBase) {
            return ((BlockFluidBase) state.getBlock()).getFluid();
        }
        return null;
    }

    public static boolean canPlayerBreakBlockPos(EntityPlayer player, BlockPos tryBreak) {
        BlockEvent.BreakEvent ev = new BlockEvent.BreakEvent(
            player.getEntityWorld(),
            tryBreak,
            player.getEntityWorld()
                .getBlockState(tryBreak),
            player);
        MinecraftForge.EVENT_BUS.post(ev);
        return !ev.isCanceled();
    }

    public static boolean canPlayerPlaceBlockPos(EntityPlayer player, EnumHand withHand, IBlockState tryPlace,
                                                 BlockPos pos, EnumFacing againstSide) {
        BlockSnapshot snapshot = new BlockSnapshot(player.getEntityWorld(), pos, tryPlace);
        BlockEvent.PlaceEvent ev = ForgeEventFactory.onPlayerBlockPlace(player, snapshot, againstSide, withHand);
        return !ev.isCanceled();
    }

    public static boolean isConnectionEstablished(EntityPlayerMP player) {
        return player.connection != null && player.connection.netManager != null
            && player.connection.netManager.isChannelOpen();
    }

    @Nullable
    public static Tuple<EnumHand, ItemStack> getMainOrOffHand(EntityLivingBase entity, Item search) {
        return getMainOrOffHand(entity, search, null);
    }

    @Nullable
    public static Tuple<EnumHand, ItemStack> getMainOrOffHand(EntityLivingBase entity, Item search,
                                                              @Nullable Predicate<ItemStack> acceptorFnc) {
        EnumHand hand = ;
        ItemStack held = entity.getHeldItem(hand);
        if (held.isEmpty() || !search.getClass()
            .isAssignableFrom(
                held.getItem()
                    .getClass())
            || (acceptorFnc != null && !acceptorFnc.test(held))) {
            hand = ;
            held = entity.getHeldItem(hand);
        }
        if (held.isEmpty() || !search.getClass()
            .isAssignableFrom(
                held.getItem()
                    .getClass())
            || (acceptorFnc != null && !acceptorFnc.test(held))) {
            return null;
        }
        return new Tuple<>(hand, held);
    }

    @Nonnull
    public static Color flareColorFromDye(EnumDyeColor color) {
        Color c = prettierColorMapping.get(color);
        if (c == null) c = Color.WHITE;
        return c;
    }

    @Nonnull
    public static TextFormatting textFormattingForDye(EnumDyeColor color) {
        switch (color) {
            case WHITE:
                return TextFormatting.WHITE;
            case ORANGE:
                return TextFormatting.GOLD;
            case MAGENTA:
                return TextFormatting.DARK_PURPLE;
            case LIGHT_BLUE:
                return TextFormatting.DARK_AQUA;
            case YELLOW:
                return TextFormatting.YELLOW;
            case LIME:
                return TextFormatting.GREEN;
            case PINK:
                return TextFormatting.LIGHT_PURPLE;
            case GRAY:
                return TextFormatting.DARK_GRAY;
            case SILVER:
                return TextFormatting.GRAY;
            case CYAN:
                return TextFormatting.BLUE;
            case PURPLE:
                return TextFormatting.DARK_PURPLE;
            case BLUE:
                return TextFormatting.DARK_BLUE;
            case BROWN:
                return TextFormatting.GOLD;
            case GREEN:
                return TextFormatting.DARK_GREEN;
            case RED:
                return TextFormatting.DARK_RED;
            case BLACK:
                return TextFormatting.DARK_GRAY; // Black is unreadable. fck that.
            default:
                return TextFormatting.WHITE;
        }
    }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return String.valueOf(Character.toTitleCase(str.charAt(0))) + str.substring(1);
    }

    public static boolean canToolBreakBlockWithoutPlayer(@Nonnull World world, @Nonnull BlockPos pos,
                                                         @Nonnull IBlockState state, @Nonnull ItemStack stack) {
        if (state.getBlockHardness(world, pos) == -1) {
            return false;
        }
        if (state.getMaterial()
            .isToolNotRequired()) {
            return true;
        }

        String tool = state.getBlock()
            .getHarvestTool(state);
        if (stack.isEmpty() || tool == null) {
            return state.getMaterial()
                .isToolNotRequired() || stack.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem()
            .getHarvestLevel(stack, tool, null, state);
        if (toolLevel < 0) {
            return state.getMaterial()
                .isToolNotRequired() || stack.canHarvestBlock(state);
        }

        return toolLevel >= state.getBlock()
            .getHarvestLevel(state);
    }

    public static boolean breakBlockWithPlayer(BlockPos pos, EntityPlayerMP playerMP) {
        return playerMP.interactionManager.tryHarvestBlock(pos);
    }

    // Copied from ForgeHooks.onBlockBreak & PlayerInteractionManager.tryHarvestBlock
    // Duplicate break functionality without a active player.
    // Emulates a FakePlayer - attempts without a player as harvester in case a fakeplayer leads to issues.
    public static boolean breakBlockWithoutPlayer(WorldServer world, BlockPos pos) {
        return breakBlockWithoutPlayer(world, pos, WorldHelper.getBlockState(world, pos), true, false, true);
    }

    public static boolean breakBlockWithoutPlayer(WorldServer world, BlockPos pos, IBlockState suggestedBrokenState,
                                                  boolean breakBlock, boolean ignoreHarvestRestrictions, boolean playEffects) {
        FakePlayer fp = AstralSorcery.proxy.getASFakePlayerServer(world);
        int exp;
        try {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, suggestedBrokenState, fp);
            MinecraftForge.EVENT_BUS.post(event);
            exp = event.getExpToDrop();
            if (event.isCanceled()) return false;
        } catch (Exception exc) {
            return false;
        }
        TileEntity tileentity = world.getTileEntity(pos);
        Block block = suggestedBrokenState.getBlock();
        if (playEffects) {
            world.playEvent(null, 2001, pos, Block.getStateId(suggestedBrokenState));
        }

        boolean harvestable = true;
        try {
            if (!ignoreHarvestRestrictions) {
                harvestable = block.canHarvestBlock(world, pos, fp);
            }
        } catch (Exception exc) {
            return false;
        }
        world.captureBlockSnapshots = true;
        try {
            if (breakBlock) {
                if (!block.removedByPlayer(suggestedBrokenState, world, pos, fp, harvestable)) {
                    world.captureBlockSnapshots = false;
                    world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
                    world.capturedBlockSnapshots.clear();
                    return false;
                }
            } else {
                block.onBlockHarvested(world, pos, suggestedBrokenState, fp);
            }
        } catch (Exception exc) {
            world.captureBlockSnapshots = false;
            world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
            world.capturedBlockSnapshots.clear();
            return false;
        }
        block.onBlockDestroyedByPlayer(world, pos, suggestedBrokenState);
        if (harvestable) {
            try {
                block.harvestBlock(world, fp, pos, suggestedBrokenState, tileentity, null);
            } catch (Exception exc) {
                world.captureBlockSnapshots = false;
                world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
                world.capturedBlockSnapshots.clear();
                return false;
            }
        }
        if (exp > 0) {
            block.dropXpOnBlockBreak(world, pos, exp);
        }
        BlockDropCaptureAssist.startCapturing();
        try {
            // Capturing block snapshots is aids. don't try that at home kids.
            world.captureBlockSnapshots = false;
            world.capturedBlockSnapshots.forEach((s) -> s.restore(true));
            world.capturedBlockSnapshots.forEach((s) -> world.setBlockToAir(s.getPos()));
            world.capturedBlockSnapshots.clear();
        } finally {
            BlockDropCaptureAssist.getCapturedStacksAndStop(); // Discard
        }
        return true;
    }

    public static void transferEntityTo(Entity entity, int targetDimId, BlockPos targetPos) {
        if (entity.getEntityWorld().isRemote) return; // No transfers on clientside.
        entity.setSneaking(false);
        if (entity.getEntityWorld().provider.dimensionId != targetDimId) {
            if (!ForgeHooks.onTravelToDimension(entity, targetDimId)) {
                return;
            }

            if (entity instanceof EntityPlayerMP) {
                FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getPlayerList()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) entity,
                        targetDimId,
                        new NoOpTeleporter(((EntityPlayerMP) entity).getServerWorld()));
            } else {
                entity.changeDimension(targetDimId);
            }
        }
        entity.setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
    }

    @Nullable
    public static BlockPos itDownTopBlock(World world, BlockPos at) {
        Chunk chunk = world.getChunkFromBlockCoords(at);
        BlockPos downPos = null;

        for (BlockPos blockpos = new BlockPos(at.getX(), chunk.getTopFilledSegment() + 16, at.getZ()); blockpos.getY()
            >= 0; blockpos = downPos) {
            downPos = blockpos.down();
            IBlockState test = WorldHelper.getBlockState(world, downPos);
            if (!world.isAirBlock(downPos) && !test.getBlock()
                .isLeaves(test, world, downPos)
                && !test.getBlock()
                .isFoliage(world, downPos)) {
                break;
            }
        }

        return downPos;
    }

    public static List<Vector3> getCirclePositions(Vector3 centerOffset, Vector3 axis, double radius,
                                                   int amountOfPointsOnCircle) {
        List<Vector3> out = new LinkedList<>();
        Vector3 circleVec = axis.clone()
            .perpendicular()
            .normalize()
            .multiply(radius);
        double degPerPoint = 360D / ((double) amountOfPointsOnCircle);
        for (int i = 0; i < amountOfPointsOnCircle; i++) {
            double deg = i * degPerPoint;
            out.add(
                circleVec.clone()
                    .rotate(Math.toRadians(deg), axis.clone())
                    .add(centerOffset));
        }
        return out;
    }

    @Nullable
    public static RayTraceResult rayTraceLook(EntityPlayer player) {
        double reach = 5D;
        if (player instanceof EntityPlayerMP) {
            reach = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
        }
        return rayTraceLook(player, reach);
    }

    @Nullable
    public static RayTraceResult rayTraceLook(EntityLivingBase entity, double reachDst) {
        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        BlockPos lookVec = entity.getLookVec();
        BlockPos end = pos.addVector(lookVec.x * reachDst, lookVec.y * reachDst, lookVec.z * reachDst);
        return entity.world.rayTraceBlocks(pos, end);
    }

    public static Color calcRandomConstellationColor(float perc) {
        return new Color(Color.HSBtoRGB((230F + (50F * perc)) / 360F, 0.8F, 0.8F - (0.3F * perc)));
    }

    public static void applyRandomOffset(Vector3 target, Random rand) {
        applyRandomOffset(target, rand, 1F);
    }

    public static void applyRandomOffset(Vector3 target, Random rand, float multiplier) {
        target.addX(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addY(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
        target.addZ(rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1));
    }

    public static boolean isChunkLoaded(World world, BlockPos pos) {
        return world.isBlockLoaded(pos);
    }

    public static boolean isChunkLoaded(World world, ChunkPos pos) {
        return world.isBlockLoaded(new BlockPos(pos.x * 16, 0, pos.z * 16));
    }

    public static boolean isPlayerFakeMP(EntityPlayerMP player) {
        if (player instanceof FakePlayer) return true;

        boolean isModdedPlayer = false;
        for (Mods mod : Mods.values()) {
            if (!mod.isPresent()) continue;
            Class<?> specificPlayerClass = mod.getExtendedPlayerClass();
            if (specificPlayerClass != null) {
                if (player.getClass() != EntityPlayerMP.class && player.getClass() == specificPlayerClass) {
                    isModdedPlayer = true;
                    break;
                }
            }
        }
        if (!isModdedPlayer && player.getClass() != EntityPlayerMP.class) {
            return true;
        }

        if (player.connection == null) return true;
        try {
            player.getPlayerIP()
                .length();
            player.connection.netManager.getRemoteAddress()
                .toString();
        } catch (Exception exc) {
            return true;
        }
        return false;
    }

    @Nullable
    public static BlockPos searchAreaForFirst(World world, BlockPos center, int radius, @Nullable Vector3 offsetFrom,
                                              BlockStateCheck acceptor) {
        return searchAreaForFirst(world, center, radius, offsetFrom, BlockStateCheck.WorldSpecific.wrap(acceptor));
    }

    @Nullable
    public static BlockPos searchAreaForFirst(World world, BlockPos center, int radius, @Nullable Vector3 offsetFrom,
                                              BlockStateCheck.WorldSpecific acceptor) {
        for (int r = 0; r <= radius; r++) {
            List<BlockPos> posList = new LinkedList<>();
            for (int xx = -r; xx <= r; xx++) {
                for (int yy = -r; yy <= r; yy++) {
                    for (int zz = -r; zz <= r; zz++) {

                        BlockPos pos = center.add(xx, yy, zz);
                        if (isChunkLoaded(world, new ChunkPos(pos))) {
                            IBlockState state = WorldHelper.getBlockState(world, pos);
                            if (acceptor.isStateValid(world, pos, state)) {
                                posList.add(pos);
                            }
                        }
                    }
                }
            }
            if (!posList.isEmpty()) {
                Vector3 offset = new Vector3(center).add(0.5, 0.5, 0.5);
                if (offsetFrom != null) {
                    offset = offsetFrom;
                }
                BlockPos closest = null;
                double prevDst = 0;
                for (BlockPos pos : posList) {
                    if (closest == null || offset.distance(pos) < prevDst) {
                        closest = pos;
                        prevDst = offset.distance(pos);
                    }
                }
                return closest;
            }
            posList.clear();
        }
        return null;
    }

    public static List<BlockPos> searchAreaFor(World world, BlockPos center, Block blockToSearch, int metaToSearch,
                                               int radius) {
        List<BlockPos> found = new LinkedList<>();
        for (int xx = -radius; xx <= radius; xx++) {
            for (int yy = -radius; yy <= radius; yy++) {
                for (int zz = -radius; zz <= radius; zz++) {
                    BlockPos pos = center.add(xx, yy, zz);
                    if (isChunkLoaded(world, new ChunkPos(pos))) {
                        IBlockState state = WorldHelper.getBlockState(world, pos);
                        Block b = state.getBlock();
                        if (b.equals(blockToSearch) && b.getMetaFromState(state) == metaToSearch) {
                            found.add(pos);
                        }
                    }
                }
            }
        }
        return found;
    }

    private static <T> Collector<T, ?, List<T>> mergeNonDuplicateList() {
        return new ListCollector<>(
            (Supplier<NonDuplicateArrayList<T>>) NonDuplicateArrayList::new,
            NonDuplicateArrayList::add,
            (left, right) -> {
                left.addAll(right);
                return left;
            });
    }

    static {
        prettierColorMapping.put(EnumDyeColor.WHITE, new Color(0xFFFFFF));
        prettierColorMapping.put(EnumDyeColor.ORANGE, new Color(0xFF8C1D));
        prettierColorMapping.put(EnumDyeColor.MAGENTA, new Color(0xEF0EFF));
        prettierColorMapping.put(EnumDyeColor.LIGHT_BLUE, new Color(0x06E5FF));
        prettierColorMapping.put(EnumDyeColor.YELLOW, new Color(0xFFEB00));
        prettierColorMapping.put(EnumDyeColor.LIME, new Color(0x93FF10));
        prettierColorMapping.put(EnumDyeColor.PINK, new Color(0xFF18D9));
        prettierColorMapping.put(EnumDyeColor.GRAY, new Color(0x5E5E5E));
        prettierColorMapping.put(EnumDyeColor.SILVER, new Color(0xBDBDBD));
        prettierColorMapping.put(EnumDyeColor.CYAN, new Color(0x5498B4));
        prettierColorMapping.put(EnumDyeColor.PURPLE, new Color(0xB721F7));
        prettierColorMapping.put(EnumDyeColor.BLUE, new Color(0x3C00FF));
        prettierColorMapping.put(EnumDyeColor.BROWN, new Color(0xB77109));
        prettierColorMapping.put(EnumDyeColor.GREEN, new Color(0x00AA00));
        prettierColorMapping.put(EnumDyeColor.RED, new Color(0xFF0000));
        prettierColorMapping.put(EnumDyeColor.BLACK, new Color(0x000000));
    }

    private static class ListCollector<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;

        public ListCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return element -> (R) element;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
        }
    }

}
