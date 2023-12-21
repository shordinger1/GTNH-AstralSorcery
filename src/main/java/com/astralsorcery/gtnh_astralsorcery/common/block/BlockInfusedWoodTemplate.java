/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package com.astralsorcery.gtnh_astralsorcery.common.block;

import static com.astralsorcery.gtnh_astralsorcery.lib.ASCreativeTabs.tabMetaBlock01;

import com.astralsorcery.gtnh_astralsorcery.lib.AstralBlocks;
import com.astralsorcery.gtnh_astralsorcery.lib.MaterialsAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockInfusedWoodTemplate
 * Created by HellFirePvP
 * Date: 20.07.2019 / 20:00
 */
public class BlockInfusedWoodTemplate extends AstralBlocks {

    public BlockInfusedWoodTemplate(String name) {
        super(name, new String[] { name }, tabMetaBlock01, MaterialsAS.INFUSED_WOOD);
    }

    // @Override
    // public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
    // return 60;
    // }
}
