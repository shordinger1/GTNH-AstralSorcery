/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.gem;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import shordinger.astralsorcery.migration.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.attribute.GemAttributeModifier;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.item.base.IItemVariants;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.MathHelper;
import shordinger.astralsorcery.migration.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemPerkGem
 * Created by HellFirePvP
 * Date: 18.11.2018 / 09:30
 */
public class ItemPerkGem extends Item implements IItemVariants {

    public ItemPerkGem() {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        PlayerProgress prog = ResearchManager.clientProgress;
        if (prog.wasOnceAttuned()) {
            for (GemAttributeModifier mod : getModifiers(stack)) {
                tooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC + mod.getLocalizedDisplayString());
            }
        } else {
            tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (GemType type : GemType.values()) {
                items.add(new ItemStack(this, 1, type.ordinal()));
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityRelic;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote) {
            return;
        }

        if (getModifiers(stack).isEmpty()) {
            GemAttributeHelper.rollGem(stack);
        }
    }

    @Nonnull
    public static List<GemAttributeModifier> getModifiers(ItemStack stack) {
        if (stack.stackSize==0 || !(stack.getItem() instanceof ItemPerkGem)) {
            return Lists.newArrayList();
        }
        List<GemAttributeModifier> modifiers = Lists.newArrayList();
        NBTTagList mods = NBTHelper.getPersistentData(stack)
            .getTagList("modifiers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < mods.tagCount(); i++) {
            NBTTagCompound tag = mods.getCompoundTagAt(i);
            modifiers.add(GemAttributeModifier.deserialize(tag));
        }
        return modifiers;
    }

    public static boolean setModifiers(ItemStack stack, List<GemAttributeModifier> modifiers) {
        if (stack.stackSize==0 || !(stack.getItem() instanceof ItemPerkGem)) {
            return false;
        }
        NBTTagList mods = new NBTTagList();
        for (GemAttributeModifier modifier : modifiers) {
            mods.appendTag(modifier.serialize());
        }
        NBTHelper.getPersistentData(stack)
            .setTag("modifiers", mods);
        return true;
    }

    @Nullable
    public static GemType getGemType(ItemStack gem) {
        if (gem.isEmpty() || !(gem.getItem() instanceof ItemPerkGem)) {
            return null;
        }
        int meta = gem.getMetadata();
        if (meta < 0 || meta >= GemType.values().length) {
            return null;
        }
        return GemType.values()[meta];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Item i = stack.getItem();
        if (i instanceof ItemPerkGem) {
            GemType type = GemType.values()[MathHelper.clamp(stack.getItemDamage(), 0, GemType.values().length)];
            return super.getUnlocalizedName(stack) + "."
                + type.name()
                .toLowerCase();
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public String[] getVariants() {
        String[] sub = new String[GemType.values().length];
        GemType[] values = GemType.values();
        for (int i = 0; i < values.length; i++) {
            GemType mt = values[i];
            sub[i] = mt.name()
                .toLowerCase();
        }
        return sub;
    }

    @Override
    public int[] getVariantMetadatas() {
        int[] sub = new int[GemType.values().length];
        GemType[] values = GemType.values();
        for (int i = 0; i < values.length; i++) {
            GemType mt = values[i];
            sub[i] = mt.ordinal();
        }
        return sub;
    }

    public static enum GemType {

        SKY(1.0F, 1.0F),
        DAY(7.5F, 0.6F),
        NIGHT(0.5F, 3.0F);

        public final float countModifier;
        public final float amplifierModifier;

        GemType(float countModifier, float amplifierModifier) {
            this.countModifier = countModifier;
            this.amplifierModifier = amplifierModifier;
        }

        public ItemStack asStack() {
            return new ItemStack(ItemsAS.perkGem, 1, ordinal());
        }
    }

}
