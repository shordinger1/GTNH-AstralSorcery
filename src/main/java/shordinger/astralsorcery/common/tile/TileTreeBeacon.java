/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.common.base.TreeTypes;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.tile.base.TileReceiverBase;
import shordinger.astralsorcery.common.util.*;
import shordinger.astralsorcery.common.util.data.NonDuplicateCappedWeightedList;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.IGrowable;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.common.util.BlockSnapshot;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTreeBeacon
 * Created by HellFirePvP
 * Date: 30.12.2016 / 13:28
 */
public class TileTreeBeacon extends TileReceiverBase implements IStructureAreaOfInfluence {

    private static final Random rand = new Random();

    private TreeCaptureHelper.TreeWatcher treeWatcher = null;
    private double starlightCharge = 0D;

    private NonDuplicateCappedWeightedList<BlockPos> treePositions = new NonDuplicateCappedWeightedList<>(
        MathHelper.floor(ConfigEntryTreeBeacon.maxCount));
    private UUID placedBy = null;

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            playEffects();
        } else {
            if (treePositions.getCap() != ConfigEntryTreeBeacon.maxCount) {
                treePositions.setCap(MathHelper.floor(ConfigEntryTreeBeacon.maxCount)); // Post-update
            }

            boolean changed = false;
            if (treeWatcher != null) {
                List<WorldBlockPos> possibleTreePositions = TreeCaptureHelper.getAndClearCachedEntries(treeWatcher);
                if (treePositions.getSize() + 30 <= ConfigEntryTreeBeacon.maxCount) {
                    if (searchForTrees(possibleTreePositions)) changed = true;
                }
            }
            int runs = MathHelper.ceil(starlightCharge * 1.4D);
            starlightCharge = 0D;
            for (int i = 0; i < Math.max(1, runs); i++) {
                if (treePositions.getSize() <= 0) continue;
                if (rand.nextFloat() >= (treePositions.getSize() / (((float) ConfigEntryTreeBeacon.maxCount) * 5F)))
                    continue;

                WRItemObject<BlockPos> randPos = treePositions
                    .getRandomElementByChance(rand, ConfigEntryTreeBeacon.speedLimiter);
                BlockPos actPos = randPos.getValue();
                if (actPos != null) {
                    TileFakeTree tft = MiscUtils.getTileAt(world, actPos, TileFakeTree.class, false);
                    if (tft != null && tft.getFakedState() != null) {
                        IBlockState fake = tft.getFakedState();
                        if (tryHarvestBlock(world, pos, actPos, fake)) { // True, if block disappeared.
                            if (world.setBlockToAir(actPos) && treePositions.removeElement(randPos)) {
                                changed = true;
                            }
                        }
                        if (ParticleEffectWatcher.INSTANCE.mayFire(world, actPos)) {
                            PktParticleEvent ev = new PktParticleEvent(
                                PktParticleEvent.ParticleEventType.TREE_VORTEX,
                                actPos);
                            PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, actPos, 32));
                        }
                    } else {
                        if (treePositions.removeElement(randPos)) {
                            changed = true;
                        }
                    }
                }
            }

            if (changed) {
                markForUpdate();
            }
        }
    }

    private boolean tryHarvestBlock(World world, BlockPos out, BlockPos treeBlockPos, IBlockState fakedState) {
        if (rand.nextInt(ConfigEntryTreeBeacon.dropsChance) == 0) {
            if (MiscUtils.canEntityTickAt(world, pos)) {
                Block b = fakedState.getBlock();
                List<ItemStack> drops = b.getDrops(world, treeBlockPos, fakedState, 2);
                for (ItemStack i : drops) {
                    if (i.isEmpty()) continue;
                    ItemUtils.dropItemNaturally(
                        world,
                        out.getX() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1),
                        out.getY() + rand.nextFloat() * 3,
                        out.getZ() + rand.nextFloat() * 3 * (rand.nextBoolean() ? 1 : -1),
                        i);
                }
            } else {
                // Don't break the block then. We do nothing.
                return false;
            }
        }
        return rand.nextInt(ConfigEntryTreeBeacon.breakChance) == 0;
    }

    private boolean searchForTrees(List<WorldBlockPos> possibleTreePositions) {
        for (WorldBlockPos possibleSapling : possibleTreePositions) {
            LogCategory.TREE_BEACON
                .info(() -> "TreeBeacon at " + getPos() + " attempt to capture tree at " + possibleSapling);
            LogCategory.TREE_BEACON.info(() -> "Existing captured snapshots: " + world.capturedBlockSnapshots.size());

            IBlockState state = possibleSapling.getStateAt();
            Block b = state.getBlock();
            if (b instanceof IGrowable) { // If it's an IGrowable, chances are it just grows to a tree when we call
                // .grow on it often enough
                if (!TreeCaptureHelper.oneTimeCatches.contains(possibleSapling))
                    TreeCaptureHelper.oneTimeCatches.add(possibleSapling);

                LogCategory.TREE_BEACON.info(() -> "Attempt IGrowable growth at " + possibleSapling);
                int tries = 8;
                world.captureBlockSnapshots = true;
                do {
                    tries--;
                    try {
                        ((IGrowable) b).grow(world, rand, possibleSapling, state);
                    } catch (Exception ignored) {
                    }
                    state = possibleSapling.getStateAt();
                    b = state.getBlock();
                } while (b instanceof IGrowable && TreeCaptureHelper.oneTimeCatches.contains(possibleSapling)
                    && tries > 0);
                world.captureBlockSnapshots = false;

                return updatePositionsFromSnapshots(world, possibleSapling, pos);
            } else if (b.getTickRandomly()) { // If it's not an IGrowable it might just grow into a tree with tons of
                // block updates.
                if (!TreeCaptureHelper.oneTimeCatches.contains(possibleSapling))
                    TreeCaptureHelper.oneTimeCatches.add(possibleSapling);

                LogCategory.TREE_BEACON.info(() -> "Attempt Block tickrandomly growth at " + possibleSapling);
                int ticksToExecute = 250;
                world.captureBlockSnapshots = true;
                do {
                    ticksToExecute--;
                    try {
                        b.updateTick(world, possibleSapling, state, rand);
                    } catch (Exception ignored) {
                    }
                    state = possibleSapling.getStateAt();
                    b = state.getBlock();
                } while (b.getTickRandomly() && TreeCaptureHelper.oneTimeCatches.contains(possibleSapling)
                    && ticksToExecute > 0);
                world.captureBlockSnapshots = false;

                return updatePositionsFromSnapshots(world, possibleSapling, pos);
            }
        }
        return false;
    }

    private boolean updatePositionsFromSnapshots(World world, WorldBlockPos saplingPos, BlockPos origin) {
        boolean ret = false;
        try {
            if (!TreeCaptureHelper.oneTimeCatches.remove(saplingPos) && !world.capturedBlockSnapshots.isEmpty()
                && world.capturedBlockSnapshots.size() > 2) { // I guess then something grew after all?
                LogCategory.TREE_BEACON.info(
                    () -> "TreeBeacon at " + getPos()
                        + " captured "
                        + world.capturedBlockSnapshots.size()
                        + " snapshots.");
                LogCategory.TREE_BEACON.info(
                    () -> "Accepts new blocks? " + (treePositions.getSize() + world.capturedBlockSnapshots.size())
                        + " <= "
                        + ConfigEntryTreeBeacon.maxCount);

                if (treePositions.getSize() + world.capturedBlockSnapshots.size() <= ConfigEntryTreeBeacon.maxCount) {
                    for (BlockSnapshot snapshot : world.capturedBlockSnapshots) {
                        IBlockState setBlock = snapshot.getCurrentBlock();
                        BlockPos at = snapshot.getPos();
                        IBlockState current = world.getBlockState(at);
                        if (current.getBlockHardness(world, at) == -1 || world.getTileEntity(at) != null) {
                            continue;
                        }
                        if (!setBlock.getBlock()
                            .equals(BlocksAS.blockFakeTree)
                            && !setBlock.getBlock()
                            .equals(Blocks.DIRT)
                            && !setBlock.getBlock()
                            .equals(Blocks.GRASS)) {
                            LogCategory.TREE_BEACON.info(() -> "Change blockstate at " + snapshot.getPos());
                            if (!world.setBlockState(snapshot.getPos(), BlocksAS.blockFakeTree.getDefaultState())) {
                                continue;
                            }
                            TileFakeTree tft = MiscUtils.getTileAt(world, snapshot.getPos(), TileFakeTree.class, true);
                            if (tft != null) {
                                tft.setupTile(origin, setBlock);
                                if (this.placedBy != null) {
                                    tft.setPlayerEffectRef(this.placedBy);
                                }
                            }
                            boolean isTreeBlock = false;
                            if (current.getBlock()
                                .isWood(world, at)) {
                                isTreeBlock = true;
                            } else {
                                TreeTypes tt = TreeTypes.getTree(world, at, current);
                                if (tt != null && tt.getLogCheck()
                                    .isStateValid(current)) {
                                    isTreeBlock = true;
                                }
                            }
                            WRItemObject<BlockPos> element = new WRItemObject<>(isTreeBlock ? 4 : 1, snapshot.getPos());
                            if (treePositions.offerElement(element)) {
                                LogCategory.TREE_BEACON.info(
                                    () -> "TreeBeacon at " + getPos() + " captured new position: " + snapshot.getPos());
                                ret = true;
                            }
                        }
                    }
                } else {
                    for (BlockSnapshot snapshot : world.capturedBlockSnapshots) {
                        IBlockState current = world.getBlockState(snapshot.getPos());
                        world.notifyBlockUpdate(snapshot.getPos(), current, current, 3);
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            world.capturedBlockSnapshots.clear();
        }
        return ret;
    }

    @SideOnly(Side.CLIENT)
    private void playEffects() {
        int color = 0xFF3FFF3F;
        PatreonEffectHelper.PatreonEffect pe;
        if (getPlacedBy() != null && (pe = PatreonEffectHelper.getPatreonEffects(Side.CLIENT, getPlacedBy())
            .stream()
            .filter(p -> p instanceof PtEffectTreeBeacon)
            .findFirst()
            .orElse(null)) != null) {
            color = ((PtEffectTreeBeacon) pe).getColorTreeEffects();
        }
        Color col = new Color(color);

        if (rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                pos.getY() + rand.nextFloat() * 2 + 0.5,
                pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion(
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.45F)
                .setColor(col)
                .gravity(0.008)
                .setMaxAge(55);
        }
        if ((ticksExisted % 32) == 0) {
            float alphaDaytime = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(world);
            alphaDaytime *= 0.8F;
            Vector3 from = new Vector3(this).add(0.5, 0.05, 0.5);
            MiscUtils.applyRandomOffset(from, EffectHandler.STATIC_EFFECT_RAND, 0.05F);
            EffectLightbeam lightbeam = EffectHandler.getInstance()
                .lightbeam(
                    from.clone()
                        .addY(7),
                    from,
                    1.5F);
            lightbeam.setAlphaMultiplier(alphaDaytime);
            lightbeam.setColorOverlay(
                col.getRed() / 255F,
                col.getGreen() / 255F,
                col.getBlue() / 255F,
                col.getAlpha() / 255F);
            lightbeam.setMaxAge(64);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        BlockPos fakeTree = event.getVec()
            .toBlockPos();
        TileFakeTree tft = MiscUtils.getTileAt(Minecraft.getMinecraft().world, fakeTree, TileFakeTree.class, false);
        if (tft != null && tft.getReference() != null) {
            TileTreeBeacon ttb = MiscUtils
                .getTileAt(Minecraft.getMinecraft().world, tft.getReference(), TileTreeBeacon.class, false);
            if (ttb != null) {
                int color = 0xFF00FF00; // Green
                PatreonEffectHelper.PatreonEffect pe;
                if (ttb.getPlacedBy() != null
                    && (pe = PatreonEffectHelper.getPatreonEffects(Side.CLIENT, ttb.getPlacedBy())
                    .stream()
                    .filter(p -> p instanceof PtEffectTreeBeacon)
                    .findFirst()
                    .orElse(null)) != null) {
                    color = ((PtEffectTreeBeacon) pe).getColorTreeDrainEffects();
                }
                Vector3 to = new Vector3(tft.getReference()).add(0.5, 0.5, 0.5);
                Color col = new Color(color, true);
                for (int i = 0; i < 10; i++) {
                    Vector3 from = new Vector3(fakeTree).add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
                    Vector3 mov = to.clone()
                        .subtract(from)
                        .normalize()
                        .multiply(0.1 + 0.1 * rand.nextFloat());
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
                    p.motion(mov.getX(), mov.getY(), mov.getZ())
                        .setMaxAge(30 + rand.nextInt(25));
                    p.gravity(0.004)
                        .scale(0.25F)
                        .setColor(col);
                }
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        TreeCaptureHelper.getAndClearCachedEntries(treeWatcher); // Prevent mem leak
        treeWatcher = null; // Clears weak ref in the TreeCaptureHelper later on automatically.
    }

    @Override
    public void validate() {
        super.validate();

        treeWatcher = new TreeCaptureHelper.TreeWatcher(
            world.provider.getDimension(),
            getPos(),
            ConfigEntryTreeBeacon.treeBeaconRange);
        TreeCaptureHelper.offerWeakWatcher(treeWatcher);
    }

    @Nullable
    @Override
    public Color getEffectRenderColor() {
        return providesEffect() ? Constellations.aevitas.getConstellationColor() : null;
    }

    @Override
    public double getRadius() {
        return providesEffect() ? ConfigEntryTreeBeacon.treeBeaconRange : 0;
    }

    @Override
    public boolean providesEffect() {
        return true;
    }

    @Override
    public int getDimensionId() {
        return this.getWorld().provider.getDimension();
    }

    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.starlightCharge = compound.getDouble("starlight");

        treePositions.clear();
        NBTTagList list = compound.getTagList("positions", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag);
            int weight = NBTHelper.getInteger(tag, "weight", 1);
            treePositions.offerElement(new WRItemObject<>(weight, pos));
        }

        if (compound.hasUniqueId("placer")) {
            this.placedBy = compound.getUniqueId("placer");
        } else {
            this.placedBy = null;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setDouble("starlight", this.starlightCharge);

        NBTTagList listPositions = new NBTTagList();
        for (WRItemObject<BlockPos> pos : treePositions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(pos.getValue(), tag);
            tag.setInteger("weight", pos.itemWeight);
            listPositions.appendTag(tag);
        }
        compound.setTag("positions", listPositions);

        if (this.placedBy != null) {
            compound.setUniqueId("placer", this.placedBy);
        }
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blocktreebeacon.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverTreeBeacon(at);
    }

    private void receiveStarlight(IWeakConstellation type, double amount) {
        this.starlightCharge += amount * 3;
        if (type == Constellations.aevitas) {
            this.starlightCharge += amount * 3;
        }
    }

    public void setPlacedBy(EntityPlayer placedBy) {
        this.placedBy = placedBy == null ? null : placedBy.getUniqueID();
    }

    public UUID getPlacedBy() {
        return placedBy;
    }

    public static class TransmissionReceiverTreeBeacon extends SimpleTransmissionReceiver {

        public TransmissionReceiverTreeBeacon(BlockPos thisPos) {
            super(thisPos);
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if (isChunkLoaded) {
                TileTreeBeacon tw = MiscUtils.getTileAt(world, getLocationPos(), TileTreeBeacon.class, false);
                if (tw != null) {
                    tw.receiveStarlight(type, amount);
                }
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new TreeBeaconReceiverProvider();
        }

    }

    public static class TreeBeaconReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverTreeBeacon provideEmptyNode() {
            return new TransmissionReceiverTreeBeacon(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverTreeBeacon";
        }

    }

    public static class ConfigEntryTreeBeacon extends ConfigEntry {

        public static final ConfigEntryTreeBeacon instance = new ConfigEntryTreeBeacon();

        public static double treeBeaconRange = 16D;
        public static int maxCount = 600;
        public static int dropsChance = 4;
        public static int breakChance = 1400;
        public static float speedLimiter = 1;

        private ConfigEntryTreeBeacon() {
            super(Section.MACHINERY, "treeBeacon");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            speedLimiter = cfg.getFloat(
                "EfficiencyLimiter",
                getConfigurationSection(),
                1F,
                0F,
                1F,
                "Percentage, how hard the speed limiter should slow down production of the tree beacon. 1=max, 0=no limiter");
            maxCount = cfg.getInt(
                "Count",
                getConfigurationSection(),
                600,
                1,
                4000,
                "Defines the amount of blocks the treeBeacon can support at max count");
            treeBeaconRange = cfg.getFloat(
                "Range",
                getConfigurationSection(),
                16F,
                4F,
                64F,
                "Defines the Range where the TreeBeacon will scan for Tree's to grow.");
            dropsChance = cfg.getInt(
                "DropsChance",
                getConfigurationSection(),
                dropsChance,
                1,
                Integer.MAX_VALUE,
                "Defines the chance that a drop is generated per random-selection tick. The higher the value the lower the chance.");
            breakChance = cfg.getInt(
                "BreakChance",
                getConfigurationSection(),
                breakChance,
                20,
                Integer.MAX_VALUE,
                "Defines the chance that the block harvested is going to break per random-selection tick. The higher the value the lower the chance");
        }

    }

}
