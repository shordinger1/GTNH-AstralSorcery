/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Sets;

import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalAxe
 * Created by HellFirePvP
 * Date: 18.09.2016 / 20:36
 */
public class ItemCrystalAxe extends ItemCrystalToolBase {

    public ItemCrystalAxe() {
        super(3);
        setDamageVsEntity(11F);
        setAttackSpeed(-3F);
        setHarvestLevel("axe", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial, maxCelestial));
            items.add(stack);
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return Sets.newHashSet("axe");
    }

}
