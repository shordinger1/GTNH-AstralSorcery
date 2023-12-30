/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block.network;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.item.block.ItemCollectorCrystal;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.migration.block.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCollectorCrystal
 * Created by HellFirePvP
 * Date: 15.09.2016 / 18:53
 */
public class BlockCelestialCollectorCrystal extends BlockCollectorCrystalBase {

    public BlockCelestialCollectorCrystal() {
        super(Material.GLASS, MapColor.CYAN);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (IWeakConstellation major : ConstellationRegistry.getWeakConstellations()) {
            ItemStack stack = new ItemStack(this);
            ItemCollectorCrystal.setConstellation(stack, major);
            ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
            list.add(stack);
        }
    }

    @Nonnull
    @Override
    public ItemStack getDecriptor(IBlockState state) {
        ItemStack stack = new ItemStack(BlocksAS.collectorCrystal);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        return stack;
    }

}
