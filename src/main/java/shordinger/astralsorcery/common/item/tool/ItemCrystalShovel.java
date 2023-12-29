/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Sets;

import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.migration.IBlockState;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 18.09.2016 / 20:34
 */
public class ItemCrystalShovel extends ItemCrystalToolBase {

    private static final Set<Block> EFFECTIVE_SET = Sets.newHashSet(
        Blocks.CLAY,
        Blocks.DIRT,
        Blocks.FARMLAND,
        Blocks.GRASS,
        Blocks.GRAVEL,
        Blocks.MYCELIUM,
        Blocks.SAND,
        Blocks.SNOW,
        Blocks.SNOW_LAYER,
        Blocks.SOUL_SAND,
        Blocks.GRASS_PATH,
        Blocks.CONCRETE_POWDER);

    public ItemCrystalShovel() {
        super(1, EFFECTIVE_SET);
        setDamageVsEntity(3F);
        setAttackSpeed(-1.5F);
        setHarvestLevel("shovel", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial));
            subItems.add(stack);
        }
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        Block block = blockIn.getBlock();

        if (block == Blocks.SNOW_LAYER) {
            return true;
        } else {
            return block == Blocks.SNOW;
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return Sets.newHashSet("shovel");
    }
}
