package com.astralsorcery.gtnh_astralsorcery.common.block;

import static com.astralsorcery.gtnh_astralsorcery.common.block.AstralBlockList.blackMarbleArch;

import com.astralsorcery.gtnh_astralsorcery.lib.AstralBlocks;

import cpw.mods.fml.common.registry.GameRegistry;

public class AstralBlockRegistry {

    public static void load() {
        GameRegistry
            .registerBlock(blackMarbleArch, AstralBlocks.innerItemBlock.class, blackMarbleArch.getLocalizedName());

    }
}
