/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.wand;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.entities.EntityGrapplingHook;
import shordinger.astralsorcery.common.item.base.render.ItemAlignmentChargeConsumer;
import shordinger.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemGrappleWand
 * Created by HellFirePvP
 * Date: 30.06.2017 / 11:23
 */
public class ItemGrappleWand extends Item implements ItemAlignmentChargeConsumer {

    public ItemGrappleWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldReveal(ChargeType ct, ItemStack stack) {
        return ct == ChargeType.TEMP;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty() || worldIn.isRemote) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        if (drainTempCharge(player, Config.grappleWandUseCost, true)) {
            worldIn.spawnEntity(new EntityGrapplingHook(worldIn, player));
            drainTempCharge(player, Config.grappleWandUseCost, false);
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

}
