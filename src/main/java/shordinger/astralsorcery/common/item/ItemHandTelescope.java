/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandTelescope
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:03
 */
public class ItemHandTelescope extends Item {

    public ItemHandTelescope() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (worldIn.isRemote) {
            AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.HAND_TELESCOPE, playerIn, worldIn, 0, 0, 0);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
    }

}
