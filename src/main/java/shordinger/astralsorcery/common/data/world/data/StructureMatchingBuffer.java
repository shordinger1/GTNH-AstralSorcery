/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.world.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.data.world.CachedWorldData;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.structure.StructureMatcher;
import shordinger.astralsorcery.common.structure.StructureMatcherRegistry;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.ChunkPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatchingBuffer
 * Created by HellFirePvP
 * Date: 02.12.2018 / 00:59
 */
public class StructureMatchingBuffer extends CachedWorldData {

    private final Map<ChunkPos, List<ChangeSubscriber<?>>> subscribers = Maps.newHashMap();
    private final Map<BlockPos, ChangeSubscriber<?>> requestSubscribers = Maps.newHashMap();

    public StructureMatchingBuffer() {
        super(WorldCacheManager.SaveKey.STRUCTURE_MATCH);
    }

    @Override
    public void updateTick(World world) {
    }

    @Nonnull
    public ChangeSubscriber<StructureMatcherPatternArray> observeAndInitializePattern(IBlockAccess world,
                                                                                      BlockPos center, PatternBlockArray pattern) {
        StructureMatcherPatternArray match = new StructureMatcherPatternArray(pattern.getRegistryName());
        match.initialize(world, center);
        return observeArea(center, match);
    }

    @Nonnull
    public <T extends StructureMatcher> ChangeSubscriber<T> observeArea(BlockPos requester, T matcher) {
        StructureMatcher regMatcher = StructureMatcherRegistry.INSTANCE.provideNewMatcher(matcher.getRegistryName());
        if (regMatcher == null) {
            AstralSorcery.log.warn(
                "Found unregistered structure matcher: " + matcher.getRegistryName()
                    .toString());
            AstralSorcery.log.warn("It will NOT persist! Register your matchers!");
        }
        ChangeSubscriber<T> subscriber = new ChangeSubscriber<>(requester, matcher);
        this.requestSubscribers.put(requester, subscriber);
        for (ChunkPos pos : subscriber.getObservableChunks()) {
            this.subscribers.computeIfAbsent(pos, (chPos) -> Lists.newArrayList())
                .add(subscriber);
        }

        markDirty();
        return subscriber;
    }

    public boolean removeSubscriber(BlockPos pos) {
        if (requestSubscribers.remove(pos) != null) {
            ChunkPos chunk = new ChunkPos(pos);
            List<ChangeSubscriber<?>> chunkSubscribers = subscribers.computeIfAbsent(chunk, ch -> Lists.newArrayList());
            chunkSubscribers.clear();
            for (ChangeSubscriber<?> subscr : requestSubscribers.values()) {
                if (subscr.getObservableChunks()
                    .contains(chunk)) {
                    chunkSubscribers.add(subscr);
                }
            }

            return true;
        }
        return false;
    }

    @Nullable
    public ChangeSubscriber<?> getSubscriber(BlockPos pos) {
        return this.requestSubscribers.get(pos);
    }

    @Nonnull
    public List<ChangeSubscriber<?>> getSubscribers(ChunkPos pos) {
        return this.subscribers.getOrDefault(pos, Collections.emptyList());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.subscribers.clear();
        this.requestSubscribers.clear();

        NBTTagList subscriberList = compound.getTagList("subscribers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < subscriberList.tagCount(); i++) {
            NBTTagCompound subscriberTag = subscriberList.getCompoundTagAt(i);

            BlockPos requester = NBTHelper.readBlockPosFromNBT(subscriberTag);
            ResourceLocation matchIdentifier = new ResourceLocation(subscriberTag.getString("identifier"));
            StructureMatcher match = StructureMatcherRegistry.INSTANCE.provideNewMatcher(matchIdentifier);
            if (match == null) {
                AstralSorcery.log
                    .warn("[Astral Sorcery] Unknown StructureMatcher: " + matchIdentifier.toString() + "! Skipping...");
                continue;
            }

            ChangeSubscriber<?> subscriber = new ChangeSubscriber<>(requester, match);
            subscriber.readFromNBT(subscriberTag.getCompoundTag("matchData"));

            this.requestSubscribers.put(subscriber.getRequester(), subscriber);
            for (ChunkPos chPos : subscriber.getObservableChunks()) {
                this.subscribers.computeIfAbsent(chPos, pos -> Lists.newArrayList())
                    .add(subscriber);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList subscriberList = new NBTTagList();

        for (ChangeSubscriber<?> sub : this.requestSubscribers.values()) {
            NBTTagCompound subscriber = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(sub.getRequester(), subscriber);
            subscriber.setString(
                "identifier",
                sub.getMatcher()
                    .getRegistryName()
                    .toString());

            NBTHelper.setAsSubTag(subscriber, "matchData", sub::writeToNBT);

            subscriberList.appendTag(subscriber);
        }

        compound.setTag("subscribers", subscriberList);
    }

}
