/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import shordinger.astralsorcery.common.item.base.IOBJItem;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.item.ItemBow;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {}

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasOBJAsSubmodelDefinition() {
        return true;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public String[] getOBJModelNames() {
        return new String[] { "ItemRoseBranchBow" };
    }

}
