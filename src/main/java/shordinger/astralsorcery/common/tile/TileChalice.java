/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.client.effect.fx.EntityFXFloatingCube;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.base.LiquidInteraction;
import shordinger.astralsorcery.common.data.config.entry.ConfigEntry;
import shordinger.astralsorcery.common.entities.EntityLiquidSpark;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.tile.base.TileEntityTick;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.RaytraceAssist;
import shordinger.astralsorcery.common.util.block.SimpleSingleFluidCapabilityTank;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.chunk.Chunk;
import shordinger.wrapper.net.minecraftforge.common.capabilities.Capability;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.fluids.Fluid;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;
import shordinger.wrapper.net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 21:58
 */
public class TileChalice extends TileEntityTick implements ILiquidStarlightPowered, IStructureAreaOfInfluence {

    private static final int TANK_SIZE = 24000;
    private SimpleSingleFluidCapabilityTank tank;

    public Vector3 rotationDegreeAxis = new Vector3();
    public Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationVecAxis1 = null, rotationVecAxis2 = null;
    private Vector3 rotationVec = null;

    private int nextTest = -1;

    public TileChalice() {
        tank = new SimpleSingleFluidCapabilityTank(TANK_SIZE, EnumFacing.DOWN);
        this.tank.setOnUpdate(this::markForUpdate);
    }

    @Override
    public void update() {
        super.update();

        if (world.isRemote) {
            if (rotationVecAxis1 == null) {
                rotationVecAxis1 = Vector3.random()
                    .multiply(360);
            }
            if (rotationVecAxis2 == null) {
                rotationVecAxis2 = Vector3.random()
                    .multiply(360);
            }
            if (rotationVec == null) {
                rotationVec = Vector3.random()
                    .normalize()
                    .multiply(1.5F);
            }

            this.prevRotationDegreeAxis = this.rotationDegreeAxis.clone();
            this.rotationDegreeAxis.add(this.rotationVec);

            playFluidEffect();
        } else {
            if (world.isBlockIndirectlyGettingPowered(getPos()) > 0) {
                return;
            }

            if (nextTest == -1) {
                nextTest = ticksExisted + 40 + rand.nextInt(90);
            }
            if (ticksExisted >= nextTest) {
                nextTest = ticksExisted + 40 + rand.nextInt(90);
                if (this.getTank()
                    .getFluid() == null
                    || this.getTank()
                    .getFluid().amount <= 0) {
                    return;
                }
                List<LiquidInteraction> interactions = LiquidInteraction.getPossibleInteractions(this.tank.getFluid());
                if (!interactions.isEmpty()) {
                    List<TileChalice> tch = collectChalicesFlat();
                    Collections.shuffle(tch);
                    for (TileChalice ch : tch) {
                        if (ch.getPos()
                            .equals(getPos())) continue;
                        if (world.isBlockIndirectlyGettingPowered(ch.pos) > 0) continue;
                        TileChalice other = MiscUtils.getTileAt(world, ch.pos, TileChalice.class, true);
                        if (other != null) {
                            if (new Vector3(this).distance(ch.getPos()) <= ConfigEntryChalice.chaliceRange) {
                                RaytraceAssist rta = new RaytraceAssist(getPos(), ch.getPos());
                                if (rta.isClear(this.world)) {
                                    FluidStack otherC = other.getTank()
                                        .getFluid();
                                    LiquidInteraction exec = LiquidInteraction
                                        .getMatchingInteraction(interactions, otherC);
                                    if (exec != null && exec.drainComponents(this, other)) {
                                        EntityLiquidSpark els1 = new EntityLiquidSpark(this.world, this.pos, exec);
                                        EntityLiquidSpark els2 = new EntityLiquidSpark(this.world, ch.getPos(), exec);

                                        els1.setTarget(els2);
                                        els1.setFluidRepresented(exec.getComponent1());

                                        els2.setTarget(els1);
                                        els2.setFluidRepresented(exec.getComponent2());

                                        this.world.spawnEntity(els1);
                                        this.world.spawnEntity(els2);

                                        this.markForUpdate();
                                        other.markForUpdate();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<TileChalice> collectChalicesFlat() {
        int ceilRange = MathHelper.ceil(ConfigEntryChalice.chaliceRange);
        BlockPos min = this.pos.add(-ceilRange, -ceilRange, -ceilRange);
        BlockPos max = this.pos.add(ceilRange, ceilRange, ceilRange);
        ChunkPos chMin = new ChunkPos(min), chMax = new ChunkPos(max);
        List<TileChalice> out = new LinkedList<>();
        for (int xx = chMin.x; xx <= chMax.x; xx++) {
            for (int zz = chMin.z; zz <= chMax.z; zz++) {
                if (MiscUtils.isChunkLoaded(world, new ChunkPos(xx, zz))) {
                    Chunk lChunk = world.getChunkFromChunkCoords(xx, zz);
                    for (TileEntity te : lChunk.getTileEntityMap()
                        .values()) {
                        if (te instanceof TileChalice && !te.isInvalid()) {
                            out.add((TileChalice) te);
                        }
                    }
                }
            }
        }
        return out;
    }

    @SideOnly(Side.CLIENT)
    private void playFluidEffect() {
        FluidStack fs = getTank().getFluid();
        if (fs == null || fs.getFluid() == null) return;

        TextureAtlasSprite tas = RenderingUtils.tryGetFlowingTextureOfFluidStack(fs);

        EntityFXFloatingCube cube;
        if (rand.nextInt(2 * (DrawSize.values().length - getDrawSize().ordinal()) * 4) == 0) {
            Vector3 at = new Vector3(this).add(0.5, 1.4, 0.5);
            at.add(
                getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1),
                getDrawSize().ordinal() * rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            cube = RenderingUtils.spawnFloatingBlockCubeParticle(at, tas);
            cube.setBlendMode(null)
                .setTextureSubSizePercentage(1F / 16F)
                .setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().world, at.toBlockPos());
            cube.setColorHandler(
                cb -> new Color(
                    fs.getFluid()
                        .getColor(fs)));
            cube.setScale(0.03F * (getDrawSize().ordinal() + 1))
                .tumble()
                .setMotion(
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.005F * (rand.nextBoolean() ? 1 : -1));
        }

        Vector3 perp = rotationVecAxis1.clone()
            .perpendicular()
            .normalize();
        perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 140D) / 140D)), rotationVecAxis1);
        perp.add(getPos())
            .add(0.5, 0.5, 0.5)
            .addY(1);

        cube = RenderingUtils.spawnFloatingBlockCubeParticle(perp, tas);
        cube.setBlendMode(null)
            .setTextureSubSizePercentage(1F / 16F)
            .setMaxAge(20 + rand.nextInt(20));
        cube.setWorldLightCoord(Minecraft.getMinecraft().world, perp.toBlockPos());
        cube.setColorHandler(
            cb -> new Color(
                fs.getFluid()
                    .getColor(fs)));
        cube.setScale(rand.nextFloat() * 0.05F + 0.05F)
            .tumble()
            .setMotion(
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(perp);
        p.setColor(Color.WHITE)
            .scale(0.1F + rand.nextFloat() * 0.05F)
            .setMaxAge(15 + rand.nextInt(10));

        if (rand.nextInt(5) == 0) {
            p = EffectHelper.genericFlareParticle(perp);
            p.setColor(Color.WHITE)
                .scale(0.1F + rand.nextFloat() * 0.1F)
                .setMaxAge(20 + rand.nextInt(20));
            p.motion(
                rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1));
        }

        if (getDrawSize().ordinal() > 1) {
            perp = rotationVecAxis2.clone()
                .perpendicular()
                .normalize();
            perp.rotate(Math.toRadians(360 * ((ClientScheduler.getClientTick() % 170D) / 170D)), rotationVecAxis2);
            perp.add(getPos())
                .add(0.5, 0.5, 0.5)
                .addY(1);

            cube = RenderingUtils.spawnFloatingBlockCubeParticle(perp, tas);
            cube.setBlendMode(null)
                .setTextureSubSizePercentage(1F / 16F)
                .setMaxAge(20 + rand.nextInt(20));
            cube.setWorldLightCoord(Minecraft.getMinecraft().world, perp.toBlockPos());
            cube.setColorHandler(
                cb -> new Color(
                    fs.getFluid()
                        .getColor(fs)));
            cube.setScale(rand.nextFloat() * 0.05F + 0.05F)
                .tumble()
                .setMotion(
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.008F * (rand.nextBoolean() ? 1 : -1));

            p = EffectHelper.genericFlareParticle(perp);
            p.setColor(Color.WHITE)
                .scale(0.05F + rand.nextFloat() * 0.05F)
                .setMaxAge(15 + rand.nextInt(5));

            if (rand.nextInt(5) == 0) {
                p = EffectHelper.genericFlareParticle(perp);
                p.setColor(Color.WHITE)
                    .scale(0.1F + rand.nextFloat() * 0.1F)
                    .setMaxAge(20 + rand.nextInt(20));
                p.motion(
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1),
                    rand.nextFloat() * 0.01 * (rand.nextBoolean() ? 1 : -1));
            }
        }
    }

    public SimpleSingleFluidCapabilityTank getTank() {
        return tank;
    }

    @Override
    public boolean canAcceptStarlight(int mbLiquidStarlight) {
        return getHeldFluid() == null || getFluidAmount() <= 0
            || (getHeldFluid() == BlocksAS.fluidLiquidStarlight && getFluidAmount() + mbLiquidStarlight <= TANK_SIZE);
    }

    @Override
    public void acceptStarlight(int mbLiquidStarlight) {
        if (canAcceptStarlight(mbLiquidStarlight)) {
            getTank().fill(new FluidStack(BlocksAS.fluidLiquidStarlight, mbLiquidStarlight), true);
            markForUpdate();
        }
    }

    @Nullable
    @Override
    public Color getEffectRenderColor() {
        return providesEffect() ? Constellations.octans.getConstellationColor() : null;
    }

    @Override
    public double getRadius() {
        return providesEffect() ? ConfigEntryChalice.chaliceRange : 0;
    }

    @Override
    public boolean providesEffect() {
        return this.getWorld()
            .isBlockIndirectlyGettingPowered(getPos()) == 0;
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
    protected void onFirstTick() {}

    public int getFluidAmount() {
        return tank.getFluidAmount();
    }

    @Nullable
    public Fluid getHeldFluid() {
        return tank.getTankFluid();
    }

    public float getPercFilled() {
        return tank.getPercentageFilled();
    }

    public DrawSize getDrawSize() {
        float perc = getPercFilled();
        if (perc >= 0.75) {
            return DrawSize.FULL;
        }
        if (perc >= 0.5) {
            return DrawSize.BIG;
        }
        if (perc >= 0.25) {
            return DrawSize.MEDIUM;
        }
        return DrawSize.SMALL;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setTag("tank", tank.writeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.tank = SimpleSingleFluidCapabilityTank.deserialize(compound.getCompoundTag("tank"));
        if (!tank.hasCapability(EnumFacing.DOWN)) {
            tank.accessibleSides.add(EnumFacing.DOWN);
        }
        this.tank.setOnUpdate(this::markForUpdate);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && tank.hasCapability(facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability != CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || !hasCapability(capability, facing))
            return null;
        return (T) tank.getCapability(facing);
    }

    public static enum DrawSize {

        SMALL(1),
        MEDIUM(2),
        BIG(4),
        FULL(8);

        public final float partTexture;
        public final int mulSize;

        DrawSize(int mulSize) {
            this.partTexture = ((float) mulSize) / 16F;
            this.mulSize = mulSize;
        }
    }

    public static class ConfigEntryChalice extends ConfigEntry {

        public static final ConfigEntryChalice instance = new ConfigEntryChalice();

        public static float chaliceRange = 16F;

        private ConfigEntryChalice() {
            super(Section.MACHINERY, "chalice");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            chaliceRange = cfg.getFloat(
                getKey() + "Range",
                getConfigurationSection(),
                chaliceRange,
                4F,
                64F,
                "Defines the Range where the Chalice look for other chalices to interact with.");
        }

    }

}
