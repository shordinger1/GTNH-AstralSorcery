package com.astralsorcery.gtnh_astralsorcery.common.block;

import static com.astralsorcery.gtnh_astralsorcery.lib.ASCreativeTabs.tabMetaBlock01;

import com.astralsorcery.gtnh_astralsorcery.lib.AstralBlocks;
import com.astralsorcery.gtnh_astralsorcery.lib.MaterialsAS;

public class BlockBlackMarbleTemplate extends AstralBlocks {

    public BlockBlackMarbleTemplate(String name) {
        super(name, new String[] { name }, tabMetaBlock01, MaterialsAS.BLACK_MARBLE);

        Properties.defaultMarble(this);
    }
}
