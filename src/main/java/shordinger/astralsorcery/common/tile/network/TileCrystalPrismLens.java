/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.block.network.BlockPrism;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import shordinger.astralsorcery.common.starlight.transmission.base.crystal.CrystalPrismTransmissionNode;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCrystalPrismLens
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:15
 */
public class TileCrystalPrismLens extends TileCrystalLens {

    @Override
    public void update() {
        super.update();

        if (world.isRemote && getLinkedPositions().size() > 0) {
            playPrismEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playPrismEffects() {
        Entity rView = Minecraft.getMinecraft()
            .getRenderViewEntity();
        if (rView == null) rView = Minecraft.getMinecraft().thePlayer;
        if (rView.getDistanceSq(getPos()) > Config.maxEffectRenderDistanceSq) return;
        Vector3 pos = new Vector3(this).add(0.5, 0.5, 0.5);
        EntityFXFacingParticle particle = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        particle.setColor(BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL.displayColor);
        particle.motion(
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1),
            rand.nextFloat() * 0.03 * (rand.nextBoolean() ? 1 : -1));
        particle.scale(0.2F);
    }

    @Override
    public ForgeDirection getPlacedAgainst() {
        IBlockState state = WorldHelper.getBlockState(world, getPos());
        if (!(state.getBlock() instanceof BlockPrism)) {
            return ForgeDirection.DOWN;
        }
        return state.getValue(BlockPrism.PLACED_AGAINST);
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.blockprism.name";
    }

    @Override
    @Nonnull
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new CrystalPrismTransmissionNode(at, getCrystalProperties());
    }
}
