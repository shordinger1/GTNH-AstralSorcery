package com.astralsorcery.gtnh_astralsorcery.common.block;

import net.minecraft.block.Block;

import com.astralsorcery.gtnh_astralsorcery.common.block.blackmarble.BlockBlackMarbleArch;
import com.astralsorcery.gtnh_astralsorcery.common.block.blackmarble.BlockBlackMarbleBricks;
import com.astralsorcery.gtnh_astralsorcery.common.block.blackmarble.BlockBlackMarbleChiseled;
import com.astralsorcery.gtnh_astralsorcery.common.block.blackmarble.BlockBlackMarbleEngraved;
import com.astralsorcery.gtnh_astralsorcery.common.block.blackmarble.BlockBlackMarbleRaw;
import com.astralsorcery.gtnh_astralsorcery.common.block.blackmarble.BlockBlackMarbleRuned;
import com.astralsorcery.gtnh_astralsorcery.common.block.infusedwood.BlockInfusedWoodArch;
import com.astralsorcery.gtnh_astralsorcery.common.block.infusedwood.BlockInfusedWoodEngraved;
import com.astralsorcery.gtnh_astralsorcery.common.block.infusedwood.BlockInfusedWoodEnriched;
import com.astralsorcery.gtnh_astralsorcery.common.block.infusedwood.BlockInfusedWoodInfused;
import com.astralsorcery.gtnh_astralsorcery.common.block.infusedwood.BlockInfusedWoodPlanks;
import com.astralsorcery.gtnh_astralsorcery.common.block.marble.BlockMarbleArch;
import com.astralsorcery.gtnh_astralsorcery.common.block.marble.BlockMarbleBricks;
import com.astralsorcery.gtnh_astralsorcery.common.block.marble.BlockMarbleChiseled;
import com.astralsorcery.gtnh_astralsorcery.common.block.marble.BlockMarbleEngraved;
import com.astralsorcery.gtnh_astralsorcery.common.block.marble.BlockMarbleRaw;
import com.astralsorcery.gtnh_astralsorcery.common.block.marble.BlockMarbleRuned;

public enum AstralBlockList {

    blackMarbleArch(new BlockBlackMarbleArch("black marble arch")),
    blackMarbleBricks(new BlockBlackMarbleBricks("black marble bricks")),
    blackMarbleChiseled(new BlockBlackMarbleChiseled("black marble chiseled")),
    blackMarbleEngraved(new BlockBlackMarbleEngraved("black marble engraved")),
    blackMarbleRaw(new BlockBlackMarbleRaw("black marble raw")),
    marbleArch(new BlockMarbleArch("marble arch")),
    blackMarbleRuned(new BlockBlackMarbleRuned("black marble runed")),
    marbleBricks(new BlockMarbleBricks("marble bricks")),
    marbleChiseled(new BlockMarbleChiseled("marble chiseled")),
    marbleEngraved(new BlockMarbleEngraved("marble engraved")),
    marbleRaw(new BlockMarbleRaw("marble raw")),
    marbleRuned(new BlockMarbleRuned("marble runed")),

    // infusedWood(new BlockInfusedWood("wood")),
    infusedWoodArch(new BlockInfusedWoodArch("wood arch")),
    infusedWoodEngraved(new BlockInfusedWoodEngraved("wood engraved")),
    infusedWoodEnriched(new BlockInfusedWoodEnriched("wood enriched")),
    infusedWoodInfused(new BlockInfusedWoodInfused("wood infused")),
    infusedWoodPlanks(new BlockInfusedWoodPlanks("wood planks"));

    private final Block block;

    AstralBlockList(Block block) {
        this.block = block;
    }

    public Block get() {
        return block;
    }
}
