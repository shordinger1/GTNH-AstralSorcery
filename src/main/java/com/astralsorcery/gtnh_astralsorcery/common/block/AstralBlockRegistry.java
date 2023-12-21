package com.astralsorcery.gtnh_astralsorcery.common.block;

import static com.astralsorcery.gtnh_astralsorcery.AstralSorcery.LOG;

import net.minecraft.block.Block;

import com.astralsorcery.gtnh_astralsorcery.lib.AstralBlocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class AstralBlockRegistry {

    public static void load() {
        for (var block : AstralBlockList.values()) {
            registry(block.get());
        }

    }

    public static void registry(Block block) {
        GameRegistry.registerBlock(block, AstralBlocks.innerItemBlock.class, block.getLocalizedName());
        LOG.info(block.getLocalizedName());

    }
}
