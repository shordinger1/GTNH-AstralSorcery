/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import shordinger.astralsorcery.client.util.RenderingUtils;
import shordinger.astralsorcery.common.auxiliary.tick.TickManager;
import shordinger.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import shordinger.astralsorcery.common.network.packet.server.PktPlayEffect;
import shordinger.astralsorcery.common.util.data.TickTokenizedMap;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.IBlockState;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBreakAssist
 * Created by HellFirePvP
 * Date: 26.07.2017 / 16:19
 */
public class BlockBreakAssist {

    private static final Map<Integer, TickTokenizedMap<BlockPos, BreakEntry>> breakMap = new HashMap<>();

    public static BreakEntry addProgress(World world, BlockPos pos, float expectedHardness, float percStrength) {
        TickTokenizedMap<BlockPos, BreakEntry> map = breakMap.get(world.provider.dimensionId);
        if (map == null) {
            map = new TickTokenizedMap<>(TickEvent.Type.SERVER);
            TickManager.getInstance()
                .register(map);
            breakMap.put(world.provider.dimensionId, map);
        }

        BreakEntry breakProgress = map.get(pos);
        if (breakProgress == null) {
            breakProgress = new BreakEntry(expectedHardness, world, pos, WorldHelper.getBlockState(world, pos));
            map.put(pos, breakProgress);
        }

        breakProgress.breakProgress -= percStrength;
        breakProgress.idleTimeout = 0;
        return breakProgress;
    }

    @SideOnly(Side.CLIENT)
    public static void blockBreakAnimation(PktPlayEffect pktPlayEffect) {
        RenderingUtils.playBlockBreakParticles(pktPlayEffect.pos, Block.getStateById(pktPlayEffect.data));
    }

    public static class BreakEntry
        implements TickTokenizedMap.TickMapToken<Float>, CEffectPositionListGen.CEffectGenListEntry {

        private float breakProgress;
        private final World world;
        private BlockPos pos;
        private IBlockState expected;

        private int idleTimeout;

        public BreakEntry(World world) {
            this.world = world;
        }

        public BreakEntry(@Nonnull Float value, World world, BlockPos at, IBlockState expectedToBreak) {
            this.breakProgress = value;
            this.world = world;
            this.pos = at;
            this.expected = expectedToBreak;
        }

        @Override
        public int getRemainingTimeout() {
            return (breakProgress <= 0 || idleTimeout >= 20) ? 0 : 1;
        }

        @Override
        public void tick() {
            idleTimeout++;
        }

        @Override
        public void onTimeout() {
            if (breakProgress > 0) return;

            IBlockState nowAt = WorldHelper.getBlockState(world, pos);
            if (MiscUtils.matchStateExact(expected, nowAt)) {
                MiscUtils.breakBlockWithoutPlayer((WorldServer) world, pos, WorldHelper.getBlockState(world, pos), true, true, true);
            }
        }

        @Override
        public Float getValue() {
            return breakProgress;
        }

        @Override
        public BlockPos pos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            this.breakProgress = nbt.getFloat("breakProgress");
            this.pos = NBTHelper.readBlockPosFromNBT(nbt);
            this.expected = Block.getStateById(nbt.getInteger("expectedStateId"));
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setFloat("breakProgress", this.breakProgress);
            NBTHelper.writeBlockPosToNBT(this.pos, nbt);
            nbt.setInteger("expectedStateId", Block.getStateId(this.expected));
        }

    }
}
