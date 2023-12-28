/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.data.world.WorldCacheManager;
import shordinger.astralsorcery.common.data.world.data.StructureMatchingBuffer;
import shordinger.astralsorcery.common.item.ItemCraftingComponent;
import shordinger.astralsorcery.common.lib.MultiBlockArrays;
import shordinger.astralsorcery.common.structure.array.PatternBlockArray;
import shordinger.astralsorcery.common.structure.change.ChangeSubscriber;
import shordinger.astralsorcery.common.structure.match.StructureMatcherPatternArray;
import shordinger.astralsorcery.common.tile.base.TileInventoryBase;
import shordinger.astralsorcery.common.util.ItemComparator;
import shordinger.astralsorcery.common.util.ItemUtils;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.PatternMatchHelper;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.BlockPos;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAttunementRelay
 * Created by HellFirePvP
 * Date: 27.03.2017 / 17:53
 */
public class TileAttunementRelay extends TileInventoryBase implements IMultiblockDependantTile {

    private static final float MAX_DST = (float) (Math.sqrt(Math.sqrt(2.0D) + 1) * 16.0D);

    private BlockPos linked = null;
    private float collectionMultiplier = 1F;

    private ChangeSubscriber<StructureMatcherPatternArray> structureMatch = null;
    private boolean canSeeSky = false, hasMultiblock = false;

    public TileAttunementRelay() {
        super(1);
    }

    public void updatePositionData(@Nullable BlockPos closestAltar, double dstSqOtherRelay) {
        this.linked = closestAltar;
        dstSqOtherRelay = Math.sqrt(dstSqOtherRelay);
        if (dstSqOtherRelay <= 1E-4) {
            collectionMultiplier = 1F;
        } else {
            collectionMultiplier = 1F - ((float) (MathHelper.clamp(dstSqOtherRelay, 0, MAX_DST) / MAX_DST));
        }
        markForUpdate();
    }

    @Override
    public void update() {
        super.update();

        if ((ticksExisted & 15) == 0) {
            updateSkyState();
        }

        ItemStack slotted = getInventoryHandler().getStackInSlot(0);
        if (!world.isRemote) {
            updateMultiblockState();

            if (!slotted.isEmpty()) {
                if (!world.isAirBlock(pos.up())) {
                    ItemStack in = getInventoryHandler().getStackInSlot(0);
                    ItemStack out = ItemUtils.copyStackWithSize(in, in.getCount());
                    ItemUtils.dropItem(world, pos.getX(), pos.getY(), pos.getZ(), out);
                    getInventoryHandler().setStackInSlot(0, null);
                }

                if (hasGlassLens()) {
                    if (linked != null && MiscUtils.isChunkLoaded(world, linked)) {
                        TileAltar ta = MiscUtils.getTileAt(world, linked, TileAltar.class, true);
                        if (ta == null) {
                            linked = null;
                            markForUpdate();
                        } else if (hasMultiblock && doesSeeSky()) {
                            WorldSkyHandler handle = ConstellationSkyHandler.getInstance()
                                .getWorldHandler(getWorld());
                            int yLevel = getPos().getY();
                            if (handle != null && yLevel > 40) {
                                double coll = 0.3;

                                float dstr;
                                if (yLevel > 120) {
                                    dstr = 1F;
                                } else {
                                    dstr = (yLevel - 40) / 80F;
                                }

                                coll *= dstr;
                                coll *= collectionMultiplier;
                                coll *= (0.2 + (0.8 * ConstellationSkyHandler.getInstance()
                                    .getCurrentDaytimeDistribution(getWorld())));
                                ta.receiveStarlight(null, coll);
                            }
                        }
                    }
                }
            }
        } else {
            if (!slotted.isEmpty() && hasMultiblock) {
                if (hasGlassLens()) {
                    if (rand.nextInt(3) == 0) {
                        Vector3 at = new Vector3(this);
                        at.add(rand.nextFloat() * 2.6 - 0.8, 0, rand.nextFloat() * 2.6 - 0.8);
                        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                        p.setAlphaMultiplier(0.7F);
                        p.setMaxAge((int) (30 + rand.nextFloat() * 50));
                        p.gravity(0.01)
                            .scale(0.3F + rand.nextFloat() * 0.1F);
                        if (rand.nextBoolean()) {
                            p.setColor(Color.WHITE);
                        }
                    }

                    if (linked != null && doesSeeSky() && rand.nextInt(4) == 0) {
                        Vector3 at = new Vector3(this);
                        Vector3 dir = new Vector3(linked).subtract(new Vector3(this))
                            .normalize()
                            .multiply(0.05);
                        at.add(
                            rand.nextFloat() * 0.4 + 0.3,
                            rand.nextFloat() * 0.3 + 0.1,
                            rand.nextFloat() * 0.4 + 0.3);
                        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                        p.setAlphaMultiplier(0.7F);
                        p.motion(dir.getX(), dir.getY(), dir.getZ());
                        p.setMaxAge((int) (15 + rand.nextFloat() * 30));
                        p.gravity(0.015)
                            .scale(0.2F + rand.nextFloat() * 0.04F);
                        if (rand.nextBoolean()) {
                            p.setColor(Color.WHITE);
                        }
                    }
                }
            }
        }
    }

    private void updateMultiblockState() {
        if (!hasGlassLens()) {
            StructureMatchingBuffer buf = WorldCacheManager
                .getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_MATCH);
            if (buf.removeSubscriber(this.pos)) {
                buf.markDirty();
            }
            if (this.structureMatch != null) {
                this.structureMatch = null;
            }
            return;
        }
        if (this.structureMatch == null) {
            this.structureMatch = PatternMatchHelper.getOrCreateMatcher(getWorld(), getPos(), getRequiredStructure());
        }
        boolean found = this.structureMatch.matches(getWorld());
        if (found != this.hasMultiblock) {
            LogCategory.STRUCTURE_MATCH.info(
                () -> "Structure match updated: " + this.getClass()
                    .getName() + " at " + this.getPos() + " (" + this.hasMultiblock + " -> " + found + ")");
            this.hasMultiblock = found;
            markForUpdate();
        }
    }

    private void updateSkyState() {
        boolean seesSky = MiscUtils.canSeeSky(this.getWorld(), this.getPos(), true, this.canSeeSky);
        boolean update = canSeeSky != seesSky;
        this.canSeeSky = seesSky;
        if (update) {
            markForUpdate();
        }
    }

    private boolean hasGlassLens() {
        ItemStack slotted = getInventoryHandler().getStackInSlot(0);
        return ItemComparator.compare(
            slotted,
            ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),
            ItemComparator.Clause.ITEM,
            ItemComparator.Clause.META_STRICT);
    }

    public boolean doesSeeSky() {
        return canSeeSky;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("seesSky", this.canSeeSky);
        compound.setBoolean("mbState", this.hasMultiblock);
        compound.setFloat("colMultiplier", this.collectionMultiplier);
        if (this.linked != null) {
            NBTHelper.setAsSubTag(compound, "linked", nbt -> NBTHelper.writeBlockPosToNBT(this.linked, nbt));
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.canSeeSky = compound.getBoolean("seesSky");
        this.hasMultiblock = compound.getBoolean("mbState");
        this.collectionMultiplier = compound.getFloat("colMultiplier");
        if (compound.hasKey("linked")) {
            this.linked = NBTHelper.readBlockPosFromNBT(compound.getCompoundTag("linked"));
        } else {
            linked = null;
        }
    }

    @Nullable
    @Override
    public PatternBlockArray getRequiredStructure() {
        return MultiBlockArrays.patternCollectorRelay;
    }

    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

}
