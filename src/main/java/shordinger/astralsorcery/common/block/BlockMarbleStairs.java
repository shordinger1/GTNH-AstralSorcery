/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import net.minecraft.block.BlockStairs;

import shordinger.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarbleStairs
 * Created by HellFirePvP
 * Date: 26.04.2017 / 10:03
 */
public class BlockMarbleStairs extends BlockStairs {

    public BlockMarbleStairs() {
        super(BlockMarble.MarbleBlockType.BRICKS.asBlock());
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setLightOpacity(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }
}
