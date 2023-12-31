/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.item.base.IOBJItem;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRoseBranchBow
 * Created by HellFirePvP
 * Date: 22.01.2017 / 15:03
 */
public class ItemRoseBranchBow extends ItemBow implements IOBJItem {

    public ItemRoseBranchBow() {
        super();
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasOBJAsSubmodelDefinition() {
        return true;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public String[] getOBJModelNames() {
        return new String[]{"ItemRoseBranchBow"};
    }

}
