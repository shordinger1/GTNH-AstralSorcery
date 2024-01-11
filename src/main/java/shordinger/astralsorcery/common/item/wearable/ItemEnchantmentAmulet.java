/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.wearable;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.common.enchantment.amulet.AmuletEnchantHelper;
import shordinger.astralsorcery.common.enchantment.amulet.AmuletEnchantment;
import shordinger.astralsorcery.common.item.base.render.ItemDynamicColor;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemEnchantmentAmulet
 * Created by HellFirePvP
 * Date: 25.01.2018 / 19:05
 */
public class ItemEnchantmentAmulet extends Item implements ItemDynamicColor, IBauble {

    private static Random rand = new Random();

    public ItemEnchantmentAmulet() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);

            items.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        List<AmuletEnchantment> enchantments = getAmuletEnchantments(stack);
        for (AmuletEnchantment ench : enchantments) {
            tooltip.add(TextFormatting.BLUE + ench.getDescription());
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && !getAmuletColor(stack).isPresent()) {
            freezeAmuletColor(stack);
        }
        if(!worldIn.isRemote && getAmuletEnchantments(stack).isEmpty()) {
            AmuletEnchantHelper.rollAmulet(stack);
        }
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if(tintIndex != 1) return 0xFFFFFFFF;
        Optional<Integer> color = getAmuletColor(stack);
        if(color.isPresent()) {
            return color.get();
        }
        int tick = (int) (ClientScheduler.getClientTick() % 500000L);
        int c = Color.getHSBColor((tick / 500000F) * 360F, 0.7F, 1F).getRGB();
        return c | 0xFF000000;
    }

    public static Optional<Integer> getAmuletColor(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Optional.empty();
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if(!tag.hasKey("amuletColor")) {
            return Optional.empty();
        }
        return Optional.of(tag.getInteger("amuletColor"));
    }

    public static void freezeAmuletColor(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if(tag.hasKey("amuletColor")) {
            return;
        }
        if(rand.nextInt(400) == 0) {
            tag.setInteger("amuletColor", 0xFFFFFFFF);
        } else {
            float hue = rand.nextFloat() * 360F;
            tag.setInteger("amuletColor", Color.getHSBColor(hue, 0.7F, 1.0F).getRGB() | 0xFF000000);
        }
    }

    public static List<AmuletEnchantment> getAmuletEnchantments(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return Lists.newArrayList();
        }

        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if(!tag.hasKey("amuletEnchantments")) {
            return Lists.newArrayList();
        }
        NBTTagList enchants = tag.getTagList("amuletEnchantments", Constants.NBT.TAG_COMPOUND);
        List<AmuletEnchantment> enchantments = new ArrayList<>(enchants.tagCount());
        for (int i = 0; i < enchants.tagCount(); i++) {
            AmuletEnchantment ench = AmuletEnchantment.deserialize(enchants.getCompoundTagAt(i));
            if(ench != null) {
                enchantments.add(ench);
            }
        }
        enchantments.sort(Comparator.comparing(AmuletEnchantment::getType));
        return enchantments;
    }

    public static void setAmuletEnchantments(ItemStack stack, List<AmuletEnchantment> enchantments) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }
        enchantments.sort(Comparator.comparing(AmuletEnchantment::getType));

        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        NBTTagList enchants = tag.hasKey("amuletEnchantments", Constants.NBT.TAG_COMPOUND) ?
                tag.getTagList("amuletEnchantments", Constants.NBT.TAG_COMPOUND) : new NBTTagList();
        for (AmuletEnchantment enchant : enchantments) {
            enchants.appendTag(enchant.serialize());
        }
        tag.setTag("amuletEnchantments", enchants);
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(SoundEvents.BLOCK_GLASS_PLACE, .65F, 6.4f);
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        player.playSound(SoundEvents.BLOCK_GLASS_PLACE, .65F, 6.4f);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

}
