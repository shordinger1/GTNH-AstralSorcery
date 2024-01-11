/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.entities.EntityCrystalTool;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.CrystalPropertyItem;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.attributes.AttributeModifier;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.ItemSword;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.NonNullList;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalSword
 * Created by HellFirePvP
 * Date: 19.09.2016 / 15:52
 */
public class ItemCrystalSword extends ItemSword implements CrystalPropertyItem {

    private static final Random rand = new Random();

    public ItemCrystalSword() {
        super(RegistryItems.crystalToolMaterial);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial));
            items.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        ToolCrystalProperties prop = getToolProperties(stack);
        CrystalProperties.addPropertyTooltip(prop, tooltip, getMaxSize(stack));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_CELESTIAL * 2;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return getToolProperties(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

    public static void setToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        properties.writeToNBT(nbt);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity ei, ItemStack itemstack) {
        EntityCrystalTool newItem = new EntityCrystalTool(ei.world, ei.posX, ei.posY, ei.posZ, itemstack);
        newItem.motionX = ei.motionX;
        newItem.motionY = ei.motionY;
        newItem.motionZ = ei.motionZ;
        newItem.setDefaultPickupDelay();
        if (ei instanceof EntityItem) {
            newItem.setThrower(((EntityItem) ei).getThrower());
            newItem.setOwner(((EntityItem) ei).getOwner());
        }
        return newItem;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, 0);
        damageProperties(stack, damage);
    }

    private void damageProperties(ItemStack stack, int damage) {
        ToolCrystalProperties prop = getToolProperties(stack);
        if (prop == null) {
            stack.setItemDamage(stack.getMaxDamage());
            return;
        }
        if (prop.getSize() <= 0) {
            super.setDamage(stack, 11);
            return;
        }
        if (damage < 0) {
            return;
        }
        for (int i = 0; i < damage; i++) {
            double chance = Math.pow(((double) prop.getCollectiveCapability()) / 100D, 2);
            if (chance >= rand.nextFloat()) {
                if (rand.nextInt(3) == 0) prop = prop.copyDamagedCutting();
                double purity = ((double) prop.getPurity()) / 100D;
                if (purity <= rand.nextFloat()) {
                    if (rand.nextInt(3) == 0) prop = prop.copyDamagedCutting();
                }
            }
        }
        setToolProperties(stack, prop);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND) {
            ToolCrystalProperties prop = getToolProperties(stack);
            if (prop != null) {
                modifiers.put(
                    SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(
                        ATTACK_DAMAGE_MODIFIER,
                        "Weapon modifier",
                        1F + (12F * prop.getEfficiencyMultiplier()),
                        0));
                modifiers.put(
                    SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -1D, 0));
            }
        }
        return modifiers;
    }

}
