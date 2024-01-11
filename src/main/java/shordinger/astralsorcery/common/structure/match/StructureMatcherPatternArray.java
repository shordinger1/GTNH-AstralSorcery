/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure.match;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import shordinger.astralsorcery.common.structure.*;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.BlockStateChangeSet;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.Vec3i;
import shordinger.wrapper.net.minecraft.world.IBlockAccess;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatcherPatternArray
 * Created by HellFirePvP
 * Date: 02.12.2018 / 13:24
 */
public class StructureMatcherPatternArray extends StructureMatcher {

    private PatternBlockArray structure;
    private ObservableArea structureArea;

    private Set<BlockPos> mismatches = new HashSet<>();

    public StructureMatcherPatternArray(@Nonnull ResourceLocation registryName) {
        super(registryName);
        setStructure(registryName);
    }

    private void setStructure(ResourceLocation structName) {
        MatchableStructure struct = StructureRegistry.INSTANCE.getStructure(structName);
        if (struct instanceof PatternBlockArray) {
            this.structure = (PatternBlockArray) struct;
            this.structureArea = new ObservableAreaBoundingBox(structure.getMin(), structure.getMax());
        } else {
            throw new IllegalArgumentException(
                "Passed structure matcher key does not have a registered underlying structure pattern: " + structName);
        }
    }

    public void initialize(IBlockAccess world, BlockPos center) {
        for (BlockPos offset : this.structure.getPattern()
            .keySet()) {
            if (!this.structure.matchSingleBlock(world, center, offset)) {
                this.mismatches.add(offset);
            }
        }
        LogCategory.STRUCTURE_MATCH.info(
            () -> "Structure matcher initialized at " + center
                + " with "
                + this.mismatches.size()
                + " initial mismatches!");
    }

    @Override
    public ObservableArea getObservableArea() {
        return this.structureArea;
    }

    @Override
    public boolean notifyChange(IBlockAccess world, BlockPos centre, BlockStateChangeSet changeSet) {
        int mismatchesPre = this.mismatches.size();

        for (BlockStateChangeSet.StateChange change : changeSet.getChanges()) {
            if (this.structure.hasBlockAt(change.pos)
                && !this.structure.matchSingleBlockState(change.pos, change.newState)) {

                this.mismatches.add(change.pos);
            } else {
                this.mismatches.remove(change.pos);
            }
        }

        this.mismatches.removeIf(mismatchPos -> !this.structure.hasBlockAt(mismatchPos));

        int mismatchesPost = this.mismatches.size();
        LogCategory.STRUCTURE_MATCH.info(
            () -> "Updated structure integrity with " + mismatchesPre
                + " mismatches before and "
                + mismatchesPost
                + " mismatches afterwards.");
        if (mismatchesPost > 0) {
            LogCategory.STRUCTURE_MATCH.info(
                () -> "Found mismatches at (relative to center): " + this.mismatches.stream()
                    .map(Vec3i::toString)
                    .collect(Collectors.joining(", ")));
        }
        return mismatchesPost <= 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.mismatches.clear();
        NBTTagList tagMismatches = tag.getTagList("mismatchList", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagMismatches.tagCount(); i++) {
            NBTTagCompound tagPos = tagMismatches.getCompoundTagAt(i);
            this.mismatches.add(NBTHelper.readBlockPosFromNBT(tagPos));
        }

        setStructure(new ResourceLocation(tag.getString("structureToMatch")));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList tagMismatches = new NBTTagList();

        for (BlockPos pos : this.mismatches) {
            NBTTagCompound tagPos = new NBTTagCompound();
            NBTHelper.writeBlockPosToNBT(pos, tagPos);
            tagMismatches.appendTag(tagPos);
        }

        tag.setTag("mismatchList", tagMismatches);

        tag.setString(
            "structureToMatch",
            this.structure.getRegistryName()
                .toString());
    }

}
