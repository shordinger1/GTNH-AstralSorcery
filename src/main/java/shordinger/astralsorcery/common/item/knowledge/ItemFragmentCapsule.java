/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.knowledge;

import java.awt.*;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.entities.EntityItemExplosionResistant;
import shordinger.astralsorcery.common.item.base.ItemHighlighted;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.SoundHelper;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemFragmentCapsule
 * Created by HellFirePvP
 * Date: 27.10.2018 / 18:53
 */
public class ItemFragmentCapsule extends Item implements ItemHighlighted {

    public ItemFragmentCapsule() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.container.desc"));
        tooltip.add(TextFormatting.GRAY + I18n.format("misc.fragment.container.open"));
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return new Color(0xCEEAFF);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityRelic;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            spawnFragment(player, hand);
        }
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            spawnFragment(player, hand);
        }
        return EnumActionResult.PASS;
    }

    private void spawnFragment(EntityPlayer player, EnumHand hand) {
        SoundHelper
            .playSoundAround(SoundEvents.BLOCK_GLASS_BREAK, player.getEntityWorld(), player.getPosition(), 0.75F, 3.5F);
        ItemStack frag = new ItemStack(ItemsAS.knowledgeFragment);
        ItemKnowledgeFragment.generateSeed(player, frag);
        player.setHeldItem(hand, frag);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityItemExplosionResistant e = new EntityItemExplosionResistant(
            world,
            location.posX,
            location.posY,
            location.posZ,
            itemstack);
        e.setDefaultPickupDelay();
        e.motionX = location.motionX;
        e.motionY = location.motionY;
        e.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            e.setThrower(((EntityItem) location).getThrower());
            e.setOwner(((EntityItem) location).getOwner());
        }
        return e;
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 300;
    }

}
