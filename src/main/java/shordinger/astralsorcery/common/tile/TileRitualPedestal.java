/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.light.EffectLightbeam;
import shordinger.astralsorcery.client.effect.texture.TextureSpritePlane;
import shordinger.astralsorcery.client.util.SpriteLibrary;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffect;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectStatus;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.NodeConnection;
import shordinger.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.common.tile.base.TileReceiverBase;
import shordinger.astralsorcery.common.util.*;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:47
 */
public class TileRitualPedestal extends TileReceiverBase
    implements IMultiblockDependantTile, IStructureAreaOfInfluence {

    public static final int MAX_EFFECT_TICK = 63;

    private TransmissionReceiverRitualPedestal cachePedestal = null;

    private Object spritePlane = null;

    private List<BlockPos> offsetMirrorPositions = new LinkedList<>();

    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean dirty = false;
    private boolean doesSeeSky = false, hasMultiblock = false;
    private BlockPos ritualLink = null;

    private int effectWorkTick = 0; // up to 63
    private boolean working = false;
    private UUID ownerUUID = null;
    private ItemStack clientCatalystCache = ItemStack.EMPTY;

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            if ((ticksExisted & 15) == 0) {
                updateSkyState(MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.doesSeeSky));

                updateLinkTile();
            }

            updateMultiblockState();

            if (dirty || !clientCatalystCache.isEmpty()) {
                TransmissionReceiverRitualPedestal recNode = getUpdateCache();
                if (recNode != null) {
                    recNode.updateSkyState(doesSeeSky);
                    recNode.updateMultiblockState(hasMultiblock);
                    recNode.updateLink(world, ritualLink);

                    boolean updated = dirty;

                    if (!clientCatalystCache.isEmpty() && recNode.getCrystal()
                        .isEmpty()) {
                        recNode.setChannelingCrystal(clientCatalystCache, this.world);
                        updated = true;
                    }

                    if (updated) {
                        recNode.markDirty(world);
                    }
                }
                dirty = false;
                markForUpdate();
            }
        }

        if (working) {
            if (effectWorkTick < 63) {
                effectWorkTick++;
            }
        } else {
            if (effectWorkTick > 0) {
                effectWorkTick--;
            }
        }

        if (world.isRemote && working) {
            float alphaDaytime = ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(world);
            alphaDaytime *= 0.8F;
            boolean isDay = alphaDaytime <= 1E-4;

            int tick = getEffectWorkTick();
            float percRunning = ((float) tick / (float) TileRitualPedestal.MAX_EFFECT_TICK);
            int chance = 15 + (int) ((1F - percRunning) * 50);
            if (rand.nextInt(chance) == 0) {
                Vector3 from = new Vector3(this).add(0.5, 0.05, 0.5);
                MiscUtils.applyRandomOffset(from, rand, 0.05F);
                EffectLightbeam lightbeam = EffectHandler.getInstance()
                    .lightbeam(
                        from.clone()
                            .addY(6),
                        from,
                        1.5F);
                lightbeam.setAlphaMultiplier(0.5F + (0.5F * alphaDaytime));
                lightbeam.setMaxAge(64);
            }
            if (ritualLink != null) {
                if (rand.nextBoolean()) {
                    Vector3 at = new Vector3(this).add(0, 0.1, 0);
                    at.add(rand.nextFloat() * 0.5 + 0.25, 0, rand.nextFloat() * 0.5 + 0.25);
                    EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                    p.setAlphaMultiplier(0.7F)
                        .setColor(Color.WHITE);
                    p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                    p.gravity(0.09)
                        .scale(0.3F + rand.nextFloat() * 0.1F);
                }
            }
            if (shouldDoAdditionalEffects() && !isDay) {
                if (rand.nextInt(chance * 2) == 0) {
                    Vector3 from = new Vector3(this).add(0.5, 0.1, 0.5);
                    MiscUtils.applyRandomOffset(from, rand, 2F);
                    from.setY(getPos().getY() - 0.6 + 1 * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
                    EffectLightbeam lightbeam = EffectHandler.getInstance()
                        .lightbeam(
                            from.clone()
                                .addY(5 + rand.nextInt(3)),
                            from,
                            1.3F);
                    lightbeam.setAlphaMultiplier(alphaDaytime);
                    if (this.getDisplayConstellation() != null) {
                        lightbeam.setColorOverlay(getDisplayConstellation().getConstellationColor());
                    }
                    lightbeam.setMaxAge(64);
                }
            }
            if (!clientCatalystCache.isEmpty() && clientCatalystCache.getItem() instanceof ItemTunedCrystalBase) {
                IWeakConstellation ch = ItemTunedCrystalBase.getMainConstellation(clientCatalystCache);
                if (ch != null) {
                    ConstellationEffect ce = ConstellationEffectRegistry.clientRenderInstance(ch);
                    if (ce != null) {
                        if (ritualLink != null) {
                            ce.playClientEffect(world, ritualLink, this, percRunning, shouldDoAdditionalEffects());
                        }
                        ce.playClientEffect(world, getPos(), this, percRunning, shouldDoAdditionalEffects());
                    }
                    CrystalProperties prop = CrystalProperties.getCrystalProperties(clientCatalystCache);
                    if (prop != null && prop.getFracturation() > 0) {
                        if (rand.nextFloat() < (prop.getFracturation() / 100F)) {
                            for (int i = 0; i < 3; i++) {
                                Vector3 at = new Vector3(this).add(0.5, 1.35, 0.5);
                                at.add(
                                    rand.nextFloat() * 0.6 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.6 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.6 * (rand.nextBoolean() ? 1 : -1));
                                Vector3 mot = new Vector3(
                                    rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1),
                                    rand.nextFloat() * 0.02 * (rand.nextBoolean() ? 1 : -1));
                                EntityFXFacingParticle p = EffectHelper
                                    .genericFlareParticle(at.getX(), at.getY(), at.getZ());
                                p.motion(mot.getX(), mot.getY(), mot.getZ());
                                p.setAlphaMultiplier(1F)
                                    .setColor(ch.getConstellationColor())
                                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                                p.setMaxAge((int) (16 + rand.nextFloat() * 15));
                                p.gravity(0.004)
                                    .scale(0.15F + rand.nextFloat() * 0.05F);
                            }

                            if (rand.nextInt(3) == 0) {
                                IWeakConstellation c = getRitualConstellation();
                                Color col = null;
                                if (c != null && c.getConstellationColor() != null) {
                                    col = c.getConstellationColor();
                                }
                                if (!offsetMirrorPositions.isEmpty()) {
                                    BlockPos to = offsetMirrorPositions.get(rand.nextInt(offsetMirrorPositions.size()));
                                    AstralSorcery.proxy.fireLightning(
                                        getWorld(),
                                        new Vector3(this).add(0.5, 1.25, 0.5),
                                        new Vector3(to).add(getPos())
                                            .add(0.5, 0.5, 0.5),
                                        col);
                                } else {
                                    AstralSorcery.proxy.fireLightning(
                                        getWorld(),
                                        new Vector3(this).add(0.5, 1.25, 0.5),
                                        new Vector3(this).add(0.5, 3.5 + rand.nextFloat() * 2.5, 0.5),
                                        col);
                                }
                            }
                        }
                    }
                }
            }
            for (BlockPos expMirror : offsetMirrorPositions) {
                if (ticksExisted % 32 == 0) {
                    Vector3 source = new Vector3(this).add(0.5, 0.75, 0.5);
                    Vector3 to = new Vector3(this).add(expMirror)
                        .add(0.5, 0.5, 0.5);
                    EffectHandler.getInstance()
                        .lightbeam(to, source, 0.8);
                    if (ritualLink != null) {
                        source = new Vector3(this).add(0.5, 5.5, 0.5);
                        EffectLightbeam beam = EffectHandler.getInstance()
                            .lightbeam(to, source, 0.8);
                        beam.setColorOverlay(Color.getHSBColor(rand.nextFloat() * 360F, 1F, 1F));
                    }
                }
            }
        }
    }

    private void updateLinkTile() {
        boolean hasLink = ritualLink != null;
        BlockPos link = getPos().add(0, 5, 0);
        TileRitualLink linkTile = MiscUtils.getTileAt(world, link, TileRitualLink.class, true);
        boolean hasLinkNow;
        if (linkTile != null) {
            this.ritualLink = linkTile.getLinkedTo();
            hasLinkNow = this.ritualLink != null;
        } else {
            hasLinkNow = false;
            this.ritualLink = null;
        }
        if (hasLink != hasLinkNow) {
            markForUpdate();
            flagDirty();
        }
    }

    public boolean isWorking() {
        return working;
    }

    public boolean hasMultiblock() {
        return hasMultiblock;
    }

    public ItemStack placeCrystalIntoPedestal(ItemStack crystal) {
        this.clientCatalystCache = ItemUtils.copyStackWithSize(crystal, Math.min(crystal.getCount(), 1));
        markForUpdate();

        TransmissionReceiverRitualPedestal recNode = getUpdateCache();
        if (recNode != null) {
            markForUpdate();
            recNode.setChannelingCrystal(crystal, this.world);
        }
        return ItemUtils.copyStackWithSize(crystal, Math.max(0, crystal.getCount() - 1));
    }

    public ItemStack getCurrentPedestalCrystal() {
        TransmissionReceiverRitualPedestal recNode = getUpdateCache();
        if (recNode != null) {
            return recNode.getCrystal();
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternRitualPedestal;
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @Override
    public double getRadius() {
        if (!providesEffect()) return 0;

        IWeakConstellation cst = getRitualConstellation();
        if (cst == null) {
            return 0;
        }
        ConstellationEffect ce = ConstellationEffectRegistry.clientRenderInstance(cst);
        if (ce == null) {
            return 0;
        }
        ConstellationEffectProperties prop = ce.provideProperties(Math.max(this.offsetMirrorPositions.size(), 0));
        if (prop != null && this.getRitualTrait() != null) {
            prop.modify(this.getRitualTrait());
        }
        return prop == null ? 0 : prop.getSize();
    }

    @Override
    public boolean providesEffect() {
        return this.working && !isInvalid();
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public Color getEffectRenderColor() {
        if (!providesEffect()) return null;

        IWeakConstellation cst = getRitualConstellation();
        if (cst == null) {
            return null;
        }
        return cst.getConstellationColor();
    }

    @Override
    public int getDimensionId() {
        return this.getWorld().provider.dimensionId;
    }

    private void updateMultiblockState() {
        if (this.structureMatch == null) {
            this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), getRequiredStructure());
        }
        boolean found = this.structureMatch.matches(getWorld());
        if (found != this.hasMultiblock) {
            LogCategory.STRUCTURE_MATCH.info(
                () -> "Structure match updated: " + this.getClass()
                    .getName() + " at " + this.getPos() + " (" + this.hasMultiblock + " -> " + found + ")");
            this.hasMultiblock = found;
            markForUpdate();
            flagDirty();
        }
    }

    // Affects only client, i'll keep the method here for misc reasons tho.
    public int getEffectWorkTick() {
        return effectWorkTick;
    }

    public ItemStack getCatalystCache() {
        return clientCatalystCache;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public IWeakConstellation getDisplayConstellation() {
        if (offsetMirrorPositions.size() != TransmissionReceiverRitualPedestal.MAX_MIRROR_COUNT) return null;
        return getRitualConstellation();
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldDoAdditionalEffects() {
        return working && offsetMirrorPositions.size() > 0;
    }

    @Nullable
    public IWeakConstellation getRitualConstellation() {
        ItemStack crystal = this.clientCatalystCache;
        if (!crystal.isEmpty() && crystal.getItem() instanceof ItemTunedCrystalBase) {
            return ItemTunedCrystalBase.getMainConstellation(crystal);
        }
        return null;
    }

    @Nullable
    public IMinorConstellation getRitualTrait() {
        ItemStack crystal = this.clientCatalystCache;
        if (!crystal.isEmpty() && crystal.getItem() instanceof ItemTunedCrystalBase) {
            return ItemTunedCrystalBase.getTrait(crystal);
        }
        return null;
    }

    @Nullable
    public TransmissionReceiverRitualPedestal getUpdateCache() {
        if (cachePedestal == null) {
            cachePedestal = tryGetNode();
        }
        if (cachePedestal != null) {
            if (!cachePedestal.getLocationPos()
                .equals(getPos())) {
                cachePedestal = null;
            }
        }
        return cachePedestal;
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if (update) {
            markForUpdate();
            flagDirty();
        }
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (!world.isRemote) {
            TransmissionReceiverRitualPedestal ped = getUpdateCache();
            if (ped != null) {
                offsetMirrorPositions.clear();
                offsetMirrorPositions.addAll(ped.offsetMirrors.keySet());
                flagDirty();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureSpritePlane getHaloEffectSprite() {
        TextureSpritePlane spr = (TextureSpritePlane) spritePlane;
        if (spr == null || spr.canRemove() || spr.isRemoved()) { // Refresh.
            spr = EffectHandler.getInstance()
                .textureSpritePlane(SpriteLibrary.spriteHalo1, Vector3.RotAxis.Y_AXIS.clone());
            spr.setPosition(new Vector3(this).add(0.5, 0.06, 0.5));
            spr.setAlphaOverDistance(true);
            spr.setNoRotation(45);
            spr.setRefreshFunc(() -> {
                if (isInvalid() || !working) {
                    return false;
                }
                if (this.getWorld().provider == null || Minecraft.getMinecraft().theWorld == null
                    || Minecraft.getMinecraft().theWorld.provider == null) {
                    return false;
                }
                return this.getWorld().provider.dimensionId
                    == Minecraft.getMinecraft().theWorld.provider.dimensionId;
            });
            spr.setScale(6.5F);
            spritePlane = spr;
        }
        return spr;
    }

    /*
     * @Override
     * protected void onInventoryChanged(int slotChanged) {
     * if(!world.isRemote) {
     * ItemStack in = getInventoryHandler().getStackInSlot(0);
     * if(!in.isEmpty() && in.getItem() instanceof ItemTunedCrystalBase) {
     * CrystalProperties properties = CrystalProperties.getCrystalProperties(in);
     * IWeakConstellation tuned = ItemTunedCrystalBase.getMainConstellation(in);
     * IMinorConstellation trait = ItemTunedCrystalBase.getTrait(in);
     * TransmissionReceiverRitualPedestal recNode = getUpdateCache();
     * if(recNode != null) {
     * recNode.updateCrystalProperties(world, properties, tuned, trait);
     * } else {
     * AstralSorcery.log.warn("Updated inventory and tried to update pedestal state.");
     * AstralSorcery.log.warn("Tried to find receiver node at dimId=" + world.provider.dimensionId + " pos=" +
     * getLocationPos() + " - couldn't find it.");
     * }
     * } else {
     * TransmissionReceiverRitualPedestal recNode = getUpdateCache();
     * if(recNode != null) {
     * recNode.updateCrystalProperties(world, null, null, null);
     * } else {
     * AstralSorcery.log.warn("Updated inventory and tried to update pedestal state.");
     * AstralSorcery.log.warn("Tried to find receiver node at dimId=" + world.provider.dimensionId + " pos=" +
     * getLocationPos() + " - couldn't find it.");
     * }
     * }
     * markForUpdate();
     * }
     * }
     */

    private void updatePositions(Collection<BlockPos> offsetMirrors) {
        offsetMirrorPositions.clear();
        offsetMirrorPositions.addAll(offsetMirrors);
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.working = compound.getBoolean("working");
        this.clientCatalystCache = NBTHelper.getStack(compound, "catalyst");
        if (compound.hasKey("ownerMost")) {
            this.ownerUUID = UUID.fromString(compound.getString("owner"));
        } else {
            this.ownerUUID = UUID.randomUUID();
        }
        this.doesSeeSky = compound.getBoolean("seesSky");
        this.hasMultiblock = compound.getBoolean("hasMultiblock");

        if (compound.hasKey("ritualLinkPos")) {
            this.ritualLink = NBTHelper.readBlockPosFromNBT(compound.getCompoundTag("ritualLinkPos"));
        } else {
            this.ritualLink = null;
        }

        offsetMirrorPositions.clear();
        NBTTagList listPos = compound.getTagList("positions", 10);
        for (int i = 0; i < listPos.tagCount(); i++) {
            offsetMirrorPositions.add(NBTHelper.readBlockPosFromNBT(listPos.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("working", working);
        NBTHelper.setStack(compound, "catalyst", this.clientCatalystCache);
        if (ownerUUID != null) {
            compound.setString("owner", String.valueOf(ownerUUID));
        }
        compound.setBoolean("hasMultiblock", hasMultiblock);
        compound.setBoolean("seesSky", doesSeeSky);

        if (ritualLink != null) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(ritualLink, tag);
            compound.setTag("ritualLinkPos", tag);
        }

        NBTTagList listPositions = new NBTTagList();
        for (BlockPos pos : offsetMirrorPositions) {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(pos, cmp);
            listPositions.appendTag(cmp);
        }
        compound.setTag("positions", listPositions);
    }

    public void flagDirty() {
        this.dirty = true;
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockritualpedestal.name";
    }

    @Override
    @Nonnull
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverRitualPedestal(at, doesSeeSky);
    }

    public void setOwner(UUID uniqueID) {
        this.ownerUUID = uniqueID;
        markForUpdate();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Nullable
    public EntityPlayer getOwningPlayerInWorld(World world) {
        UUID uuid = getOwnerUUID();
        return uuid == null ? null : world.getPlayerEntityByUUID(uuid);
    }

    public static class TransmissionReceiverRitualPedestal extends SimpleTransmissionReceiver {

        private static final int MAX_MIRROR_COUNT = 5;

        // private static final int[] secToNext = new int[] { 12_000, 30_000, 60_000, 144_000, 360_000 };
        private static final int[] secToNext = new int[]{10, 10, 6, 10, 10};
        // private static final int[] chanceToNext = new int[] { 50, 200, 500, 1000, 2000 };
        private static final int[] chanceToNext = new int[]{2, 2, 2, 2, 2};

        private static final BlockPos[] possibleOffsets = new BlockPos[]{new BlockPos(4, 2, 0), new BlockPos(4, 2, 1),
            new BlockPos(3, 2, 2), new BlockPos(2, 2, 3), new BlockPos(1, 2, 4), new BlockPos(0, 2, 4),
            new BlockPos(-1, 2, 4), new BlockPos(-2, 2, 3), new BlockPos(-3, 2, 2), new BlockPos(-4, 2, 1),
            new BlockPos(-4, 2, 0), new BlockPos(-4, 2, -1), new BlockPos(-3, 2, -2), new BlockPos(-2, 2, -3),
            new BlockPos(-1, 2, -4), new BlockPos(0, 2, -4), new BlockPos(1, 2, -4), new BlockPos(2, 2, -3),
            new BlockPos(3, 2, -2), new BlockPos(4, 2, -1)};

        private int ticksTicking = 0;

        private boolean doesSeeSky, hasMultiblock;
        private BlockPos ritualLinkTo = null;
        private IWeakConstellation channeling;
        private IMinorConstellation trait;
        private CrystalProperties properties;
        private int channeled = 0;

        private ItemStack crystal = ItemStack.EMPTY;

        // private TreeCaptureHelper.TreeWatcher tw = null;
        private ConstellationEffect ce;
        private Map<BlockPos, Boolean> offsetMirrors = new HashMap<>();

        private double collectionChannelBuffer = 0D;
        private boolean doesWorkBuffer = false;
        private float posDistribution = -1;

        private int idleBuffer = 0;

        public TransmissionReceiverRitualPedestal(BlockPos thisPos, boolean doesSeeSky) {
            super(thisPos);
            this.doesSeeSky = doesSeeSky;
        }

        @Override
        public void update(World world) {
            ticksTicking++;

            if (!this.crystal.isEmpty() && this.crystal.getItem() instanceof ItemTunedCrystalBase) {
                CrystalProperties properties = CrystalProperties.getCrystalProperties(this.crystal);
                IWeakConstellation tuned = ItemTunedCrystalBase.getMainConstellation(this.crystal);
                IMinorConstellation trait = ItemTunedCrystalBase.getTrait(this.crystal);
                updateCrystalProperties(world, properties, tuned, trait);
            } else {
                updateCrystalProperties(world, null, null, null);
            }

            if (channeling != null && properties != null && hasMultiblock) {
                if (ce == null) {
                    ce = channeling.getRitualEffect(getRitualOrigin());
                    /*
                     * if(channeling.equals(Constellations.ara)) {
                     * tw = new TreeCaptureHelper.TreeWatcher(world.provider.dimensionId, getLocationPos(),
                     * CEffectAra.treeRange);
                     * if(CEffectAra.enabled) {
                     * TreeCaptureHelper.offerWeakWatcher(tw);
                     * ((CEffectAra) ce).refTreeWatcher = new WeakReference<>(tw);
                     * }
                     * }
                     */
                }
                /*
                 * if(channeling != Constellations.ara) {
                 * tw = null;
                 * }
                 */

                if (ticksTicking % 20 == 0) {
                    WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
                    List<BlockPos> toNodes = getSources();
                    for (BlockPos pos : new LinkedList<>(offsetMirrors.keySet())) {
                        BlockPos act = pos.add(getLocationPos());
                        if (!toNodes.contains(act)) {
                            offsetMirrors.put(pos, false);
                            continue;
                        }

                        IPrismTransmissionNode node = handle.getTransmissionNode(act);
                        if (node == null) continue;

                        boolean found = false;
                        for (NodeConnection<IPrismTransmissionNode> n : node.queryNext(handle)) {
                            if (n.getTo()
                                .equals(getLocationPos())) {
                                offsetMirrors.put(pos, n.canConnect());
                                found = true;
                            }
                        }
                        if (!found) {
                            offsetMirrors.put(pos, false);
                        }
                    }
                }

                if (ticksTicking % 60 == 0) {
                    TileRitualPedestal pedestal = getTileAtPos(world, TileRitualPedestal.class);
                    if (pedestal != null) {
                        if (pedestal.offsetMirrorPositions.size() != offsetMirrors.size()) {
                            updateMirrorPositions(world);
                        }
                    }
                }

                if (doesSeeSky) {
                    double perc = 0.2D + (0.8D * ConstellationSkyHandler.getInstance()
                        .getCurrentDaytimeDistribution(world));
                    WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
                        .getWorldHandler(world);

                    if (posDistribution == -1) {
                        posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, getLocationPos());
                    }

                    if (handle != null) {
                        perc *= CrystalCalculations.getCollectionAmt(
                            properties,
                            handle.getCurrentDistribution(channeling, (in) -> 0.2F + (0.8F * in)));
                        perc *= 1 + (0.5 * posDistribution);
                    }
                    collectionChannelBuffer += perc / 2D;
                }
                if (collectionChannelBuffer > 0) {
                    idleBuffer = 0;

                    doMainEffect(world, ce, trait);

                    if (tryIncrementChannelingTimer()) channeled++;

                    flagAsWorking(world);
                } else {
                    if (idleBuffer > 2) {
                        flagAsInactive(world);
                        ce = null;
                    } else {
                        idleBuffer++;
                    }
                }
            } else {
                if (idleBuffer > 2) {
                    flagAsInactive(world);
                    ce = null;
                } else {
                    idleBuffer++;
                }
            }
        }

        private void doMainEffect(World world, ConstellationEffect ce, @Nullable IMinorConstellation trait) {
            ConstellationEffectProperties prop = ce.provideProperties(getCollectedBackmirrors());
            if (trait != null) {
                prop = prop.modify(trait);
            }

            double maxDrain = 14D;
            maxDrain /= CrystalCalculations.getMaxRitualReduction(this.properties);
            maxDrain /= Math.max(1, getCollectedBackmirrors() - 1);
            collectionChannelBuffer *= prop.getPotency();
            int executeTimes = MathHelper.floor(collectionChannelBuffer / maxDrain);

            int freeCap = MathHelper.floor(
                CrystalCalculations.getChannelingCapacity(this.properties)
                    * prop.getFracturationLowerBoundaryMultiplier());
            double addFractureChance = CrystalCalculations.getFractureChance(executeTimes, freeCap)
                * CrystalCalculations.getCstFractureModifier(this.channeling)
                * prop.getFracturationRate();
            int part = Math.max(1, executeTimes - freeCap);

            if (ce instanceof ConstellationEffectStatus && collectionChannelBuffer > 0) {
                collectionChannelBuffer = 0;
                BlockPos to = getLocationPos();
                if (ritualLinkTo != null) to = ritualLinkTo;
                if (((ConstellationEffectStatus) ce).runEffect(world, to, getCollectedBackmirrors(), prop, trait)) {
                    for (int i = 0; i < part; i++) {
                        if (rand.nextFloat() < (addFractureChance * prop.getEffectAmplifier() / part)) {
                            fractureCrystal(world);
                        }
                    }
                    markDirty(world);
                }
                return;
            }

            executeTimes = MathHelper.floor(executeTimes * prop.getEffectAmplifier());
            for (int i = 0; i <= executeTimes; i++) {
                if (collectionChannelBuffer >= maxDrain) {
                    collectionChannelBuffer -= maxDrain;
                } else {
                    collectionChannelBuffer = 0F;
                }

                BlockPos to = getLocationPos();
                if (ritualLinkTo != null) to = ritualLinkTo;
                if (ce.playEffect(world, to, 1F, prop, trait)) {
                    if (rand.nextFloat() < (addFractureChance * prop.getEffectAmplifier() / part)) {
                        fractureCrystal(world);
                    }
                    markDirty(world);
                }
            }
        }

        private void fractureCrystal(World world) {
            if (!this.crystal.isEmpty()) {
                CrystalProperties prop = CrystalProperties.getCrystalProperties(this.crystal);
                if (prop != null) {
                    prop = new CrystalProperties(
                        prop.getSize(),
                        prop.getPurity(),
                        prop.getCollectiveCapability(),
                        prop.getFracturation() + 1,
                        prop.getSizeOverride());
                    if (prop.getFracturation() >= 100) {
                        SoundHelper.playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, world, getLocationPos(), 7.5F, 1.4F);
                        Vector3 at = new Vector3(getLocationPos()).add(0.5, 1.5, 0.5);
                        PktParticleEvent ev = new PktParticleEvent(
                            PktParticleEvent.ParticleEventType.CELESTIAL_CRYSTAL_BURST,
                            at);
                        PacketChannel.CHANNEL
                            .sendToAllAround(ev, PacketChannel.pointFromPos(world, getLocationPos(), 32));
                        this.crystal = ItemStack.EMPTY;
                    } else {
                        CrystalProperties.applyCrystalProperties(this.crystal, prop);
                    }
                    markDirty(world);
                }
            }
        }

        private int getCollectedBackmirrors() {
            int amt = 1;
            for (boolean f : offsetMirrors.values()) if (f) amt++;
            return amt;
        }

        private void flagAsInactive(World world) {
            if (doesWorkBuffer) {
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if (ped != null) {
                    doesWorkBuffer = false;
                    channeled = 0;

                    ped.working = false;
                    ped.markForUpdate();

                    clearAllMirrorPositions(world);
                }
            }
        }

        private void flagAsWorking(World world) {
            if (!doesWorkBuffer) {
                TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
                if (ped != null) {

                    doesWorkBuffer = true;
                    ped.working = true;
                    ped.markForUpdate();
                }
            }
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, IWeakConstellation type, double amount) {
            if (channeling != null && hasMultiblock) {
                if (channeling.equals(type)) {
                    collectionChannelBuffer += amount;
                    tryGainMirrorPos(world);
                }
            }
        }

        private boolean tryIncrementChannelingTimer() {
            if (offsetMirrors.size() < 0 || offsetMirrors.size() >= 5) return false;
            if ((getCollectedBackmirrors() - 1) < offsetMirrors.size()) return false;
            int step = secToNext[offsetMirrors.size()];
            return channeled <= step;
        }

        private void tryGainMirrorPos(World world) {
            if (offsetMirrors.size() < 0 || offsetMirrors.size() >= 5) return;
            int mirrors = offsetMirrors.size();
            if ((getCollectedBackmirrors() - 1) < mirrors) return;
            int step = secToNext[mirrors];
            if (channeled > step) {
                if (world.rand.nextInt(chanceToNext[mirrors]) == 0) {
                    findPossibleMirror(world);
                }
            }
        }

        private void findPossibleMirror(World world) {
            long seed = 3451968351053166105L;
            seed |= this.getLocationPos()
                .toLong() * 31;
            seed |= this.channeling.getUnlocalizedName()
                .hashCode() * 31;
            Random r = new Random(seed);
            for (int i = 0; i < this.getCollectedBackmirrors(); i++) {
                r.nextInt(possibleOffsets.length);
            }
            BlockPos offset = null;
            boolean isValid = false;
            int c = 100;
            lblWhile:
            while (!isValid && c > 0) {
                c--;
                offset = possibleOffsets[r.nextInt(possibleOffsets.length)];
                RaytraceAssist ray = new RaytraceAssist(getLocationPos(), getLocationPos().add(offset));
                Vector3 from = new Vector3(0.5, 0.7, 0.5);
                Vector3 newDir = new Vector3(offset).add(0.5, 0.5, 0.5)
                    .subtract(from);
                for (BlockPos p : offsetMirrors.keySet()) {
                    Vector3 toDir = new Vector3(p).add(0.5, 0.5, 0.5)
                        .subtract(from);
                    if (Math.toDegrees(toDir.angle(newDir)) <= 30) {
                        continue lblWhile;
                    }
                    if (offset.distanceSq(p) <= 3) {
                        continue lblWhile;
                    }
                    if (!ray.isClear(world)) {
                        continue lblWhile;
                    }
                }
                isValid = true;
            }
            if (isValid) {
                addMirrorPosition(world, offset);
            }
        }

        public void addMirrorPosition(World world, BlockPos offset) {
            this.offsetMirrors.put(offset, false);
            updateMirrorPositions(world);

            markDirty(world);
        }

        public void clearAllMirrorPositions(World world) {
            this.offsetMirrors.clear();
            updateMirrorPositions(world);

            markDirty(world);
        }

        @Override
        public boolean needsUpdate() {
            return true;
        }

        public void updateMirrorPositions(World world) {
            TileRitualPedestal ped = getTileAtPos(world, TileRitualPedestal.class);
            if (ped != null) {
                ped.updatePositions(offsetMirrors.keySet());
            }
        }

        private ILocatable getRitualOrigin() {
            if (this.ritualLinkTo == null) {
                return this;
            }
            return ILocatable.fromPos(this.ritualLinkTo);
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);

            doesSeeSky = compound.getBoolean("doesSeeSky");
            hasMultiblock = compound.getBoolean("hasMultiblock");
            channeled = compound.getInteger("channeled");
            properties = CrystalProperties.readFromNBT(compound);
            IConstellation c = IConstellation.readFromNBT(compound, IConstellation.getDefaultSaveKey() + "Normal");
            if (c != null && !(c instanceof IWeakConstellation)) {
                AstralSorcery.log.warn(
                    "Tried to load RitualPedestal from NBT with a non-Major constellation as effect. Ignoring constellation...");
                AstralSorcery.log.warn("Block affected is at " + getLocationPos());
            } else if (c == null) {
                channeling = null;
            } else {
                channeling = (IWeakConstellation) c;
            }
            c = IConstellation.readFromNBT(compound, IConstellation.getDefaultSaveKey() + "Trait");
            if (c != null && !(c instanceof IMinorConstellation)) {
                AstralSorcery.log.warn(
                    "Tried to load RitualPedestal from NBT with a non-Minor constellation as trait. Ignoring constellation...");
                AstralSorcery.log.warn("Block affected is at " + getLocationPos());
            } else if (c == null) {
                trait = null;
            } else {
                trait = (IMinorConstellation) c;
            }

            offsetMirrors.clear();
            NBTTagList listPos = compound.getTagList("positions", 10);
            for (int i = 0; i < listPos.tagCount(); i++) {
                offsetMirrors.put(NBTHelper.readBlockPosFromNBT(listPos.getCompoundTagAt(i)), false);
            }

            if (compound.hasKey("crystal")) {
                this.crystal = new ItemStack(compound.getCompoundTag("crystal"));
            } else {
                this.crystal = ItemStack.EMPTY;
            }

            if (compound.hasKey("ritualLinkPos")) {
                this.ritualLinkTo = NBTHelper.readBlockPosFromNBT(compound.getCompoundTag("ritualLinkPos"));
            } else {
                this.ritualLinkTo = null;
            }

            if (channeling != null) {
                ce = channeling.getRitualEffect(getRitualOrigin());
                if (compound.hasKey("effect") && ce != null) {
                    NBTTagCompound cmp = compound.getCompoundTag("effect");
                    ce.readFromNBT(cmp);
                }
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);

            compound.setBoolean("doesSeeSky", doesSeeSky);
            compound.setBoolean("hasMultiblock", hasMultiblock);
            compound.setInteger("channeled", channeled);

            NBTTagList listPositions = new NBTTagList();
            for (BlockPos pos : offsetMirrors.keySet()) {
                NBTTagCompound cmp = new NBTTagCompound();
                NBTHelper.writeBlockPosToNBT(pos, cmp);
                listPositions.appendTag(cmp);
            }
            compound.setTag("positions", listPositions);

            if (properties != null) {
                properties.writeToNBT(compound);
            }
            if (channeling != null) {
                channeling.writeToNBT(compound, IConstellation.getDefaultSaveKey() + "Normal");
            }
            if (!crystal.isEmpty()) {
                NBTHelper.setAsSubTag(compound, "crystal", this.crystal::writeToNBT);
            }
            if (trait != null) {
                trait.writeToNBT(compound, IConstellation.getDefaultSaveKey() + "Trait");
            }
            if (ritualLinkTo != null) {
                NBTHelper
                    .setAsSubTag(compound, "ritualLinkPos", nbt -> NBTHelper.writeBlockPosToNBT(ritualLinkTo, nbt));
            }
            if (ce != null) {
                NBTHelper.setAsSubTag(compound, "effect", this.ce::writeToNBT);
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new PedestalReceiverProvider();
        }

        /*
         * public void update(boolean doesSeeSky, Constellation bufferChanneling, Constellation trait) {
         * this.doesSeeSky = doesSeeSky;
         * this.channeling = bufferChanneling;
         * this.trait = trait;
         * }
         */

        public void updateSkyState(boolean doesSeeSky) {
            this.doesSeeSky = doesSeeSky;
        }

        public void updateMultiblockState(boolean hasMultiblock) {
            this.hasMultiblock = hasMultiblock;
        }

        public void updateCrystalProperties(World world, CrystalProperties properties, IWeakConstellation channeling,
                                            IMinorConstellation trait) {
            IWeakConstellation prevChannel = this.channeling;
            CrystalProperties prevProp = this.properties;
            IMinorConstellation prevTrait = this.trait;
            this.properties = properties;
            this.channeling = channeling;
            this.trait = trait;
            if (this.channeling != prevChannel) {
                this.clearAllMirrorPositions(world);
            }

            if (this.channeling != prevChannel || this.trait != prevTrait
                || (!Objects.equals(this.properties, prevProp))) {
                markDirty(world);
            }
        }

        public void updateLink(@Nonnull World world, @Nullable BlockPos ritualLink) {
            BlockPos prev = this.ritualLinkTo;
            this.ritualLinkTo = ritualLink;
            if (prev == null && this.ritualLinkTo == null) return; // Wtf.
            if (prev == null || !prev.equals(this.ritualLinkTo)) {
                if (channeling != null) {
                    this.ce = channeling.getRitualEffect(getRitualOrigin());
                }
                markDirty(world);
            }
        }

        public void setChannelingCrystal(ItemStack crystal, World world) {
            this.crystal = ItemUtils.copyStackWithSize(crystal, Math.min(crystal.getCount(), 1));
            markDirty(world);
        }

        public ItemStack getCrystal() {
            return crystal;
        }
    }

    public static class PedestalReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverRitualPedestal provideEmptyNode() {
            return new TransmissionReceiverRitualPedestal(null, false);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverRitualPedestal";
        }

    }

}
