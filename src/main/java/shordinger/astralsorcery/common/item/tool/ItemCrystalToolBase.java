/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.tool;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.entities.EntityCrystalTool;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.CrystalPropertyItem;
import shordinger.astralsorcery.common.item.crystal.ToolCrystalProperties;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.astralsorcery.migration.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalTool
 * Created by HellFirePvP
 * Date: 18.09.2016 / 12:25
 */
public abstract class ItemCrystalToolBase extends ItemTool implements CrystalPropertyItem {

    private static final Random rand = new Random();
    private final int crystalCount;

    public ItemCrystalToolBase(int crystalCount) {
        this(crystalCount, Collections.emptySet());
    }

    public ItemCrystalToolBase(int crystalCount, Set<Block> effectiveBlocksIn) {
        super(0, 0, RegistryItems.crystalToolMaterial, effectiveBlocksIn);
        setMaxDamage(0);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        this.crystalCount = crystalCount;
    }

    public void setDamageVsEntity(float damageVsEntity) {
        this.attackDamage = damageVsEntity;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getCrystalCount() {
        return crystalCount;
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
        return CrystalProperties.MAX_SIZE_CELESTIAL * getCrystalCount();
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return getToolProperties(stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        float str = super.getDestroySpeed(stack, state);
        ToolCrystalProperties properties = getToolProperties(stack);
        return str * properties.getEfficiencyMultiplier() * 2F;
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
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return false;
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
                if (rand.nextInt(8) == 0) prop = prop.copyDamagedCutting();
                double purity = ((double) prop.getPurity()) / 100D;
                if (purity <= rand.nextFloat()) {
                    if (rand.nextInt(8) == 0) prop = prop.copyDamagedCutting();
                }
            }
        }
        setToolProperties(stack, prop);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 10;
    }

}
