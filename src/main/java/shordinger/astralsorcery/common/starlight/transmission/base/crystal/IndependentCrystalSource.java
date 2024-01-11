/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base.crystal;

import java.util.Map;
import java.util.function.Function;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.starlight.IIndependentStarlightSource;
import shordinger.astralsorcery.common.starlight.IStarlightSource;
import shordinger.astralsorcery.common.starlight.transmission.base.SimpleIndependentSource;
import shordinger.astralsorcery.common.starlight.transmission.registry.SourceClassRegistry;
import shordinger.astralsorcery.common.tile.base.TileNetworkSkybound;
import shordinger.astralsorcery.common.tile.base.TileSourceBase;
import shordinger.astralsorcery.common.tile.network.TileCollectorCrystal;
import shordinger.astralsorcery.common.util.CrystalCalculations;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.SkyCollectionHelper;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IndependentCrystalSource
 * Created by HellFirePvP
 * Date: 04.08.2016 / 15:01
 */
public class IndependentCrystalSource extends SimpleIndependentSource {

    public static final double MIN_DST = 16;

    private CrystalProperties crystalProperties;
    private BlockCollectorCrystalBase.CollectorCrystalType type;
    private boolean doesSeeSky, structural;
    private double collectionDstMultiplier = 1;
    private float posDistribution = -1;

    private boolean enhanced = false;

    public IndependentCrystalSource(CrystalProperties properties, IWeakConstellation constellation, boolean seesSky,
                                    boolean hasBeenLinkedBefore, BlockCollectorCrystalBase.CollectorCrystalType type) {
        super(constellation);
        this.crystalProperties = properties;
        this.doesSeeSky = seesSky;
        this.type = type;
        this.structural = hasBeenLinkedBefore;
    }

    @Override
    public float produceStarlightTick(World world, BlockPos pos) {
        IWeakConstellation cst = getStarlightType();
        if (cst == null) return 0F;
        if (crystalProperties == null || type == null) return 0F;

        WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
            .getWorldHandler(world);
        if (!doesSeeSky || handle == null) {
            return 0F;
        }

        if (posDistribution == -1) {
            posDistribution = SkyCollectionHelper.getSkyNoiseDistribution(world, pos);
        }

        Function<Float, Float> distrFunction = getDistributionFunc();
        double perc = distrFunction.apply(
            ConstellationSkyHandler.getInstance()
                .getCurrentDaytimeDistribution(world));
        perc *= collectionDstMultiplier;
        perc *= 1 + (0.3 * posDistribution);
        return (float) (perc * CrystalCalculations
            .getCollectionAmt(crystalProperties, handle.getCurrentDistribution(cst, distrFunction)));
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }

    private Function<Float, Float> getDistributionFunc() {
        if (enhanced) {
            return (in) -> 0.6F + (1.1F * in);
        } else {
            return (in) -> 0.2F + (0.8F * in);
        }
    }

    @Override
    public boolean providesAutoLink() {
        return structural;
    }

    @Override
    public void informTileStateChange(IStarlightSource sourceTile) {
        TileNetworkSkybound tns = MiscUtils
            .getTileAt(sourceTile.getTrWorld(), sourceTile.getTrPos(), TileNetworkSkybound.class, true);
        if (tns != null) {
            this.doesSeeSky = tns.doesSeeSky();
        }
        if (tns instanceof TileSourceBase && ((TileSourceBase) tns).hasBeenLinked()) {
            this.structural = true;
        }
        if (tns instanceof TileCollectorCrystal) {
            this.crystalProperties = ((TileCollectorCrystal) tns).getCrystalProperties();
            this.type = ((TileCollectorCrystal) tns).getType();
            this.starlightType = ((TileCollectorCrystal) tns).getConstellation();
        }
    }

    @Override
    public void threadedUpdateProximity(BlockPos thisPos, Map<BlockPos, IIndependentStarlightSource> otherSources) {
        double minDstSq = Double.MAX_VALUE;
        for (BlockPos other : otherSources.keySet()) {
            if (other.equals(thisPos)) continue;
            double dstSq = thisPos.distanceSq(other);
            if (dstSq < minDstSq) {
                minDstSq = dstSq;
            }
        }
        double dst = Math.sqrt(minDstSq);
        if (dst <= MIN_DST) {
            collectionDstMultiplier = dst / MIN_DST;
        } else {
            collectionDstMultiplier = 1;
        }
    }

    @Override
    public SourceClassRegistry.SourceProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.crystalProperties = CrystalProperties.readFromNBT(compound);
        this.doesSeeSky = compound.getBoolean("seesSky");
        this.structural = compound.getBoolean("linkedBefore");
        this.type = BlockCollectorCrystalBase.CollectorCrystalType.values()[compound.getInteger("collectorType")];
        this.collectionDstMultiplier = compound.getDouble("dstMul");
        this.enhanced = compound.getBoolean("enhanced");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (crystalProperties != null) {
            crystalProperties.writeToNBT(compound);
        }
        compound.setBoolean("seesSky", doesSeeSky);
        compound.setBoolean("linkedBefore", structural);
        compound.setInteger("collectorType", type == null ? 0 : type.ordinal());
        compound.setDouble("dstMul", collectionDstMultiplier);
        compound.setBoolean("enhanced", enhanced);
    }

    public static class Provider implements SourceClassRegistry.SourceProvider {

        @Override
        public IIndependentStarlightSource provideEmptySource() {
            return new IndependentCrystalSource(null, null, false, false, null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":IndependentCrystalSource";
        }

    }

}
