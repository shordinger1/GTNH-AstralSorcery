/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.world.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import shordinger.astralsorcery.common.data.world.CachedWorldData;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.tile.TileCelestialGateway;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GatewayCache
 * Created by HellFirePvP
 * Date: 16.04.2017 / 18:05
 */
public class GatewayCache extends CachedWorldData {

    private List<GatewayNode> gatewayPositions = new LinkedList<>();

    public GatewayCache() {
        super(WorldCacheManager.SaveKey.GATEWAY_DATA);
    }

    public List<GatewayNode> getGatewayPositions() {
        return new ArrayList<>(gatewayPositions);
    }

    public void offerPosition(World world, BlockPos pos, @Nonnull String display) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null || !(te instanceof TileCelestialGateway)) {
            return;
        }
        GatewayNode node = new GatewayNode(pos, display);
        if (gatewayPositions.contains(node)) {
            return;
        }
        gatewayPositions.add(node);
        markDirty();
        CelestialGatewaySystem.instance.addPosition(world, node);
        AstralSorcery.log
            .info("Added new gateway node at: dim=" + world.provider.dimensionId + ", " + pos.toString());
    }

    public void removePosition(World world, BlockPos pos) {
        if (gatewayPositions.remove(pos)) {
            markDirty();
            CelestialGatewaySystem.instance.removePosition(world, pos);
            AstralSorcery.log
                .info("Removed gateway node at: dim=" + world.provider.dimensionId + ", " + pos.toString());
        }
    }

    @Override
    public void onLoad(World world) {
        AstralSorcery.log.info("Checking GatewayCache integrity for dimension " + world.provider.dimensionId);
        long msStart = System.currentTimeMillis();

        Iterator<GatewayNode> iterator = gatewayPositions.iterator();
        while (iterator.hasNext()) {
            GatewayNode node = iterator.next();
            TileCelestialGateway gateway;
            try {
                gateway = MiscUtils.getTileAt(world, node, TileCelestialGateway.class, true);
            } catch (Exception loadEx) {
                AstralSorcery.log.info("Failed to check gateway for " + node + " skipping");
                continue;
            }
            if (gateway == null) {
                iterator.remove();
                AstralSorcery.log.info("Invalid entry: " + node + " - no gateway tileentity found there!");
            }
        }

        AstralSorcery.log.info(
            "GatewayCache checked and fully loaded in " + (System.currentTimeMillis() - msStart)
                + "ms! Collected and checked "
                + gatewayPositions.size()
                + " gateway nodes!");
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("posList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos pos = NBTHelper.readBlockPosFromNBT(tag);
            String display = tag.getString("display");
            GatewayNode node = new GatewayNode(pos, display);
            gatewayPositions.add(node);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (GatewayNode node : gatewayPositions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(node, tag);
            tag.setString("display", node.display);
            list.appendTag(tag);
        }
        compound.setTag("posList", list);
    }

    @Override
    public void updateTick(World world) {}

    public static class GatewayNode extends BlockPos {

        public final String display;

        public GatewayNode(BlockPos pos, String display) {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.display = display;
        }

        @Override
        public boolean equals(Object pos) {
            return super.equals(pos);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }

}
