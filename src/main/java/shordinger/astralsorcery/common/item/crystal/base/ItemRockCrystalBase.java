/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.crystal.base;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.entities.EntityCrystal;
import shordinger.astralsorcery.common.item.base.ItemHighlighted;
import shordinger.astralsorcery.common.item.crystal.CrystalProperties;
import shordinger.astralsorcery.common.item.crystal.CrystalPropertyItem;
import shordinger.astralsorcery.common.item.crystal.ItemCelestialCrystal;
import shordinger.astralsorcery.common.item.crystal.ItemTunedCelestialCrystal;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.registry.RegistryItems;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public abstract class ItemRockCrystalBase extends Item implements ItemHighlighted, CrystalPropertyItem {

    private static Random rand = new Random();

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        CrystalProperties prop = CrystalProperties.getCrystalProperties(stack);
        if (prop == null) {
            Item i = stack.getItem();
            if (i instanceof ItemCelestialCrystal || i instanceof ItemTunedCelestialCrystal) {
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.createRandomCelestial());
            } else {
                CrystalProperties.applyCrystalProperties(stack, CrystalProperties.createRandomRock());
            }
        } else {
            if (prop.getFracturation() >= 100) {
                stack.setCount(0);
                entityIn.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.5F, rand.nextFloat() * 0.2F + 0.8F);
            }
        }
    }

    @Override
    public Color getHightlightColor(ItemStack stack) {
        return Color.WHITE;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        EntityCrystal crystal = new EntityCrystal(world, location.posX, location.posY, location.posZ, itemstack);
        crystal.setDefaultPickupDelay();
        crystal.setNoDespawn();
        crystal.motionX = location.motionX;
        crystal.motionY = location.motionY;
        crystal.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            crystal.setThrower(((EntityItem) location).getThrower());
            crystal.setOwner(((EntityItem) location).getOwner());
        }
        return crystal;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        addCrystalPropertyToolTip(stack, tooltip);
    }

    @Override
    public int getMaxSize(ItemStack stack) {
        return CrystalProperties.MAX_SIZE_ROCK;
    }

    @Nullable
    @Override
    public CrystalProperties provideCurrentPropertiesOrNull(ItemStack stack) {
        return CrystalProperties.getCrystalProperties(stack);
    }

    @SideOnly(Side.CLIENT)
    protected Optional<Boolean> addCrystalPropertyToolTip(ItemStack stack, List<String> tooltip) {
        return CrystalProperties
            .addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip, getMaxSize(stack));
    }

    public abstract ItemTunedCrystalBase getTunedItemVariant();

    public static ItemStack createMaxBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.getMaxRockProperties());
        return crystal;
    }

    public static ItemStack createMaxCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.getMaxCelestialProperties());
        return crystal;
    }

    public static ItemStack createRandomBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomRock());
        return crystal;
    }

    public static ItemStack createRandomCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomCelestial());
        return crystal;
    }

}
