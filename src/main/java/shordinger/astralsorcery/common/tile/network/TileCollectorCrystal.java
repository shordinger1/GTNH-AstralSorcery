/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.network;

import java.awt.*;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHandler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.EntityComplexFX;
import shordinger.astralsorcery.client.effect.controller.orbital.OrbitalEffectCollector;
import shordinger.astralsorcery.client.effect.controller.orbital.OrbitalEffectController;
import shordinger.astralsorcery.client.effect.fx.EntityFXBurst;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectCorruptedCelestialCrystal;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;
import shordinger.astralsorcery.common.starlight.WorldNetworkHandler;
import shordinger.astralsorcery.common.starlight.transmission.ITransmissionSource;
import shordinger.astralsorcery.common.starlight.transmission.base.SimpleTransmissionSourceNode;
import shordinger.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.common.tile.IMultiblockDependantTile;
import shordinger.astralsorcery.common.tile.IStructureAreaOfInfluence;
import shordinger.astralsorcery.common.tile.base.TileSourceBase;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.PatternMatchHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:25
 */
public class TileCollectorCrystal extends TileSourceBase
    implements IMultiblockDependantTile, IStructureAreaOfInfluence {

    private static final UUID DUMMY_UUID = UUID.fromString("0cd550cc-8341-4b96-8d1e-d4a12deb8ca3");
    public static final BlockPos[] offsetsLiquidStarlight = new BlockPos[]{new BlockPos(-1, -4, -1),
        new BlockPos(0, -4, -1), new BlockPos(1, -4, -1), new BlockPos(1, -4, 0), new BlockPos(1, -4, 1),
        new BlockPos(0, -4, 1), new BlockPos(-1, -4, 1), new BlockPos(-1, -4, 0),};

    private static final Random rand = new Random();

    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private BlockCollectorCrystalBase.CollectorCrystalType type;
    private CrystalProperties usedCrystalProperties;
    private UUID playerRef;
    private boolean multiBlockPresent = false;
    private IWeakConstellation associatedType;
    private IMinorConstellation associatedTrait;

    private Object[] orbitals = new Object[4];

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            if (ticksExisted > 4 && associatedType == null) {
                if (!getWorld().setBlockToAir(getPos())) {
                    return;
                }
            }
            if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
                if (this.structureMatch == null) {
                    this.structureMatch = PatternMatchHelper
                        .getOrCreateMatcher(getWorld(), getPos(), getRequiredStructure());
                }
                boolean found = this.structureMatch.matches(getWorld());
                if (found != this.multiBlockPresent) {
                    LogCategory.STRUCTURE_MATCH.info(
                        () -> "Structure match updated: " + this.getClass()
                            .getName() + " at " + this.getPos() + " (" + this.multiBlockPresent + " -> " + found + ")");
                    this.multiBlockPresent = found;
                    setEnhanced(found);
                    markForUpdate();
                }
            }
        } else {
            if (!doesSeeSky()) {
                EntityFXFacingParticle p = EffectHelper
                    .genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
                p.motion(
                    (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.04F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
                p.scale(0.2F)
                    .setMaxAge(35);
                Color c = Color.WHITE;
                if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
                    if (playerRef != null && MiscUtils.contains(
                        PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerRef),
                        pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                        c = Color.RED;
                    } else {
                        c = Color.CYAN;
                    }
                }
                p.setColor(c);
            } else {
                if (isEnhanced() && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL
                    && associatedType != null) {
                    playEnhancedEffects();
                }
            }
        }
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            return MultiBlockArrays.patternCollectorEnhancement;
        }
        return null;
    }

    @Nonnull
    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @SideOnly(Side.CLIENT)
    private void playEnhancedEffects() {
        if (Minecraft.isFancyGraphicsEnabled()) {
            EntityFXFacingParticle p = EffectHelper
                .genericFlareParticle(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
            p.motion(
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
            p.scale(0.25F)
                .setMaxAge(25);

            Color c = Color.CYAN;
            if (playerRef != null && MiscUtils.contains(
                PatreonEffectHelper.getPatreonEffects(Side.CLIENT, playerRef),
                pe -> pe instanceof PtEffectCorruptedCelestialCrystal)) {
                c = Color.RED;
            }
            p.setColor(c);
        }

        for (int i = 0; i < orbitals.length; i++) {
            OrbitalEffectController ctrl = (OrbitalEffectController) orbitals[i];
            if (ctrl == null) {
                OrbitalEffectCollector prop = new OrbitalEffectCollector(this);
                ctrl = EffectHandler.getInstance()
                    .orbital(prop, null, null);
                ctrl.setOffset(new Vector3(this).add(0.5, 0.5, 0.5));
                ctrl.setOrbitRadius(0.8 + rand.nextFloat() * 0.4);
                ctrl.setOrbitAxis(Vector3.random());
                ctrl.setTicksPerRotation(60);
                orbitals[i] = ctrl;
            } else {
                if (ctrl.canRemove() || ctrl.isRemoved()) {
                    orbitals[i] = null;
                }
            }
        }

        BlockPos randomPos = offsetsLiquidStarlight[rand.nextInt(offsetsLiquidStarlight.length)].add(pos);
        Vector3 from = new Vector3(randomPos).add(rand.nextFloat(), 0.8, rand.nextFloat());
        Vector3 to = new Vector3(this).add(0.5, 0.5, 0.5);
        Vector3 mov = to.clone()
            .subtract(from)
            .normalize()
            .multiply(0.1);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
        p.motion(mov.getX(), mov.getY(), mov.getZ())
            .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
        p.gravity(0.004)
            .scale(0.25F)
            .setMaxAge(30 + rand.nextInt(10));
        Color c;
        switch (rand.nextInt(4)) {
            case 0:
                c = Color.WHITE;
                break;
            case 1:
                c = associatedType.getConstellationColor()
                    .brighter();
                break;
            case 2:
            default:
                c = associatedType.getConstellationColor();
                break;
        }
        p.setColor(c);

        if (usedCrystalProperties != null
            && (usedCrystalProperties.getPurity() > 90 || usedCrystalProperties.getCollectiveCapability() > 90)
            && rand.nextInt(100) == 0) {
            AstralSorcery.proxy.fireLightning(world, to, from, c);
        }
    }

    @Override
    public boolean onSelect(EntityPlayer player) {
        if (player.isSneaking()) {
            for (BlockPos linkTo : Lists.newArrayList(getLinkedPositions())) {
                tryUnlink(player, linkTo);
            }
            player.sendMessage(
                new TextComponentTranslation("misc.link.unlink.all")
                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Color getEffectRenderColor() {
        return providesEffect() ? Color.WHITE : null;
    }

    @Override
    public double getRadius() {
        return providesEffect() ? 16 : 0;
    }

    @Override
    public boolean providesEffect() {
        return this.doesSeeSky();
    }

    @Override
    public int getDimensionId() {
        return this.getWorld().provider.dimensionId;
    }

    public boolean isPlayerMade() {
        return playerRef != null;
    }

    public UUID getPlayerReference() {
        return playerRef;
    }

    public CrystalProperties getCrystalProperties() {
        return usedCrystalProperties;
    }

    public IWeakConstellation getConstellation() {
        return associatedType;
    }

    public IMinorConstellation getTrait() {
        return associatedTrait;
    }

    public void onPlace(IWeakConstellation constellation, @Nullable IMinorConstellation trait,
                        CrystalProperties properties, @Nullable UUID player, BlockCollectorCrystalBase.CollectorCrystalType type) {
        this.associatedType = constellation;
        this.associatedTrait = trait;
        this.playerRef = player;
        this.usedCrystalProperties = properties;
        this.type = type;

        this.needsUpdate = true;
        markDirty();
    }

    public void setEnhanced(boolean enhanced) {
        if (!world.isRemote && type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            this.multiBlockPresent = enhanced;
            WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
            IIndependentStarlightSource source = handle.getSourceAt(getPos());
            if (source instanceof IndependentCrystalSource) {
                ((IndependentCrystalSource) source).setEnhanced(enhanced);
                handle.markDirty();
            }
            markForUpdate();
        }
    }

    public boolean isEnhanced() {
        return multiBlockPresent;
    }

    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        BlockPos at = event.getVec()
            .toBlockPos();
        EffectHandler.getInstance()
            .registerFX(new EntityFXBurst(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, 3F));
    }

    public static void breakDamage(World world, BlockPos pos) {
    }

    public BlockCollectorCrystalBase.CollectorCrystalType getType() {
        return type;
    }

    @Override
    public boolean hasBeenLinked() {
        return playerRef == null;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasUniqueId("playerRef")) {
            this.playerRef = compound.getUniqueId("playerRef");
        } else if (compound.hasKey("player") && compound.getBoolean("player")) {
            this.playerRef = DUMMY_UUID; // Legacy data conversion..
        } else {
            this.playerRef = null;
        }
        this.associatedType = (IWeakConstellation) IConstellation.readFromNBT(compound);
        this.associatedTrait = (IMinorConstellation) IConstellation
            .readFromNBT(compound, IConstellation.getDefaultSaveKey() + "trait");
        this.usedCrystalProperties = CrystalProperties.readFromNBT(compound);
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInteger("collectorType")];
        this.multiBlockPresent = compound.hasKey("enhanced") ? compound.getBoolean("enhanced")
            : compound.getBoolean("multiBlockPresent");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        if (this.playerRef != null) {
            compound.setUniqueId("playerRef", this.playerRef);
        }
        if (associatedType != null) {
            associatedType.writeToNBT(compound);
        }
        if (associatedTrait != null) {
            associatedTrait.writeToNBT(compound, IConstellation.getDefaultSaveKey() + "trait");
        }
        if (usedCrystalProperties != null) {
            usedCrystalProperties.writeToNBT(compound);
        }
        if (type != null) {
            compound.setInteger("collectorType", type.ordinal());
        }
        compound.setBoolean("multiBlockPresent", multiBlockPresent);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return Block.FULL_BLOCK_AABB.grow(1)
            .offset(getPos());
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockcollectorcrystal.name";
    }

    @Override
    @Nonnull
    public IIndependentStarlightSource provideNewSourceNode() {
        return new IndependentCrystalSource(usedCrystalProperties, associatedType, doesSeeSky, hasBeenLinked(), type);
    }

    @Override
    @Nonnull
    public ITransmissionSource provideSourceNode(BlockPos at) {
        return new SimpleTransmissionSourceNode(at);
    }

}
