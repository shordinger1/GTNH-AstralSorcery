/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataActiveCelestials
 * Created by HellFirePvP
 * Date: 09.05.2016 / 21:46
 */
public class DataActiveCelestials extends AbstractData {

    private final Map<Integer, List<IConstellation>> activeConstellations = new HashMap<>();
    private final List<Integer> updateRequested = new LinkedList<>();

    @Nullable
    public Collection<IConstellation> getActiveConstellations(int dimId) {
        return activeConstellations.get(dimId);
    }

    public void setNewConstellations(int dimId, Collection<IConstellation> constellations) {
        activeConstellations.put(dimId, new LinkedList<>(constellations));

        requestUpdate(dimId);
        markDirty();
    }

    private void requestUpdate(int dimId) {
        if (!updateRequested.contains(dimId)) updateRequested.add(dimId);
        markDirty();
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        for (Integer i : activeConstellations.keySet()) {
            addDimensionConstellations(i, compound);
        }
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        for (Integer i : updateRequested) {
            addDimensionConstellations(i, compound);
        }
    }

    private void addDimensionConstellations(int dimId, NBTTagCompound mainCompound) {
        NBTTagList list = new NBTTagList();
        Collection<IConstellation> csts = getActiveConstellations(dimId);
        if (csts != null) {
            for (IConstellation c : csts) {
                list.appendTag(new NBTTagString(c.getUnlocalizedName()));
            }
        }
        mainCompound.setTag(String.valueOf(dimId), list);
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        for (String dimIdStr : compound.getKeySet()) {
            int dimId;
            try {
                dimId = Integer.parseInt(dimIdStr);
            } catch (Exception exc) {
                AstralSorcery.log
                    .warn("Received ConstellationUpdate packet with a non-integer dimensionId: " + dimIdStr);
                AstralSorcery.log.warn("Skipping...");
                continue;
            }
            NBTTagList list = compound.getTagList(dimIdStr, 8);
            List<IConstellation> toUpdate = new LinkedList<>();
            if (list.tagCount() != 0) {
                for (int i = 0; i < list.tagCount(); i++) {
                    String str = list.getStringTagAt(i);
                    IConstellation c = ConstellationRegistry.getConstellationByName(str);
                    if (c == null) {
                        AstralSorcery.log.warn("Received unknown constellation from server: " + str);
                    } else {
                        toUpdate.add(c);
                    }
                }
            }
            this.activeConstellations.put(dimId, toUpdate);
        }
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if (!(serverData instanceof DataActiveCelestials)) return;

        Map<Integer, List<IConstellation>> update = ((DataActiveCelestials) serverData).activeConstellations;
        this.activeConstellations.putAll(update);
    }

    public static class Provider extends ProviderAutoAllocate<DataActiveCelestials> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataActiveCelestials provideNewInstance(Side side) {
            return new DataActiveCelestials();
        }

    }

}
