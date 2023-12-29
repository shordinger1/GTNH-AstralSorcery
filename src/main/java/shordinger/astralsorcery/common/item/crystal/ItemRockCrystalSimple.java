/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.crystal;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalSimple
 * Created by HellFirePvP
 * Date: 15.09.2016 / 14:25
 */
public class ItemRockCrystalSimple extends ItemRockCrystalBase {

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxRockProperties());
            subItems.add(stack);
        }
    }

    @Override
    public ItemTunedCrystalBase getTunedItemVariant() {
        return ItemsAS.tunedRockCrystal;
    }
}