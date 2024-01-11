/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.effect.aoe;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.base.TileAccelerationBlacklist;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.effect.CEffectPositionList;
import shordinger.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import shordinger.astralsorcery.common.constellation.effect.GenListEntries;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.network.PacketChannel;
import shordinger.astralsorcery.common.network.packet.server.PktParticleEvent;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.ParticleEffectWatcher;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.effect.time.TimeStopController;
import shordinger.astralsorcery.common.util.effect.time.TimeStopZone;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.ITickable;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.ChunkPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.config.Configuration;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectHorologium
 * Created by HellFirePvP
 * Date: 10.01.2017 / 18:34
 */
public class CEffectHorologium extends CEffectPositionList {

    //public static final int MAX_SEARCH_RANGE = 8;
    //public static final int MAX_ACCEL_COUNT = 30;

    public boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 16;
    public static int maxCount = 30;

    public CEffectHorologium(@Nullable ILocatable origin) {
        super(origin, Constellations.horologium, "horologium", maxCount, (world, pos) -> TileAccelerationBlacklist.canAccelerate(world.getTileEntity(pos)));
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        if(modified.isCorrupted()) {
            TimeStopZone zone = TimeStopController.tryGetZoneAt(world, pos);
            if(zone == null) {
                zone = TimeStopController.freezeWorldAt(TimeStopZone.EntityTargetController.noPlayers(), world, pos, true, (float) modified.getSize(), 100);
            }
            if(zone == null) {
                return false;
            }
            zone.setTicksToLive(100);
            return true;
        }
        boolean changed = false;
        GenListEntries.SimpleBlockPosEntry entry = getRandomElementByChance(rand);
        if(entry != null) {
            BlockPos sel = entry.getPos();
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(sel))) {
                TileEntity te = world.getTileEntity(sel);
                if(TileAccelerationBlacklist.canAccelerate(te)) { //Does != null && instanceof ITickable check.
                    if (ParticleEffectWatcher.INSTANCE.mayFire(world, sel)) {
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_ACCEL_TILE, sel.getX(), sel.getY(), sel.getZ());
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, sel, 16));
                    }
                    try {
                        long startNs = System.nanoTime();
                        int times = 5 + rand.nextInt(3);
                        while (times > 0) {
                            ((ITickable) te).update();
                            if((System.nanoTime() - startNs) >= 80_000) {
                                break;
                            }
                            times--;
                        }
                    } catch (Exception exc) {
                        TileAccelerationBlacklist.errored(te.getClass());
                        removeElement(entry);
                        AstralSorcery.log.warn("Couldn't accelerate TileEntity " + te.getClass().getName() + " properly.");
                        AstralSorcery.log.warn("Temporarily blacklisting that class. Consider adding that to the blacklist if it persists?");
                        exc.printStackTrace();
                    }
                } else {
                    removeElement(entry);
                    changed = true;
                }
            }
        }

        if(findNewPosition(world, pos, modified)) changed = true;

        return changed;
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                at.getX() + 0.5,
                at.getY() + 0.5,
                at.getZ() + 0.5);
        p.motion((rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.03F) * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.25F).setColor(Color.CYAN.brighter());
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectHorologium.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), searchRange, 1, 64, "Defines the radius (in blocks) in which the ritual will search for valid tileEntities to accelerate");
        maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 30, 1, 4000, "Defines the amount of tileEntities the ritual can cache and accelerate at max count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }
}
