/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.crystal;

import java.awt.*;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.item.base.render.ItemGatedVisibility;
import shordinger.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCelestialCrystal
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:49
 */
public class ItemTunedCelestialCrystal extends ItemTunedCrystalBase implements ItemGatedVisibility {

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack;
            for (IWeakConstellation c : ConstellationRegistry.getWeakConstellations()) {
                stack = new ItemStack(this);
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
                applyMainConstellation(stack, c);
                subItems.add(stack);
            }
        }
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL;
    }

    @Override
    public ItemTunedCrystalBase getTunedItemVariant() {
        return this;
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL.displayColor;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityCelestial;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isSupposedToSeeInRender(ItemStack stack) {
        return getClientProgress().getTierReached()
            .isThisLaterOrEqual(ProgressionTier.CONSTELLATION_CRAFT);
    }

}
