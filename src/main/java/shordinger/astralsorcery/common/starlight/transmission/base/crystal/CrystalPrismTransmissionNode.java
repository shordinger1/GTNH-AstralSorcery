/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission.base.crystal;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.base.SimplePrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import shordinger.astralsorcery.common.tile.network.TileCrystalLens;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPrismTransmissionNode
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:07
 */
public class CrystalPrismTransmissionNode extends SimplePrismTransmissionNode {

    private CrystalProperties properties;
    private float additionalLoss = 1F;

    public CrystalPrismTransmissionNode(BlockPos thisPos, @Nullable CrystalProperties properties) {
        super(thisPos);
        this.properties = properties;
    }

    public CrystalPrismTransmissionNode(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public boolean needsTransmissionUpdate() {
        return true;
    }

    @Override
    public void onTransmissionTick(World world) {
        TileCrystalLens lens = MiscUtils.getTileAt(world, getLocationPos(), TileCrystalLens.class, false);
        if(lens != null) {
            lens.onTransmissionTick();
        }
    }

    public void updateAdditionalLoss(float loss) {
        this.additionalLoss = loss;
    }

    @Override
    public float getAdditionalTransmissionLossMultiplier() {
        return additionalLoss;
    }

    @Override
    @Nullable
    public CrystalProperties getTransmissionProperties() {
        return properties;
    }

    @Override
    public TransmissionClassRegistry.TransmissionProvider getProvider() {
        return new Provider();
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("size") && compound.hasKey("purity") && compound.hasKey("collect")) {
            this.properties = CrystalProperties.readFromNBT(compound);
        } else {
            this.properties = null;
        }
        this.additionalLoss = compound.getFloat("lossMultiplier");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (this.properties != null) {
            this.properties.writeToNBT(compound);
        }
        compound.setFloat("lossMultiplier", this.additionalLoss);
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new CrystalPrismTransmissionNode(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":CrystalPrismTransmissionNode";
        }

    }

}
