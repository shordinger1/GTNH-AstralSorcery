/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.block;

import javax.annotation.Nullable;

import shordinger.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.entities.EntityItemHighlighted;
import shordinger.astralsorcery.common.item.base.ItemHighlighted;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.util.nbt.NBTHelper;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.item.EnumRarity;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCollectorCrystal
 * Created by HellFirePvP
 * Date: 01.08.2016 / 13:10
 */
public class ItemCollectorCrystal extends ItemBlockCustomName implements ItemHighlighted {

    public ItemCollectorCrystal(BlockCollectorCrystalBase collectorCrystalBase) {
        super(collectorCrystalBase);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorceryTunedCrystals);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        EntityItemHighlighted ei = new EntityItemHighlighted(world, entity.posX, entity.posY, entity.posZ, itemstack);
        BlockCollectorCrystalBase.CollectorCrystalType type = getType(itemstack);
        ei.applyColor(type.displayColor);
        ei.setDefaultPickupDelay();
        ei.motionX = entity.motionX;
        ei.motionY = entity.motionY;
        ei.motionZ = entity.motionZ;
        if (entity instanceof EntityItem) {
            ei.setThrower(((EntityItem) entity).getThrower());
            ei.setOwner(((EntityItem) entity).getOwner());
        }
        return ei;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        BlockCollectorCrystalBase.CollectorCrystalType type = getType(stack);
        if (type == BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL) {
            return RegistryItems.rarityCelestial;
        }
        return super.getRarity(stack);
    }

    public static void setType(ItemStack stack, BlockCollectorCrystalBase.CollectorCrystalType type) {
        NBTHelper.getPersistentData(stack)
            .setInteger("collectorType", type.ordinal());
    }

    public static BlockCollectorCrystalBase.CollectorCrystalType getType(ItemStack stack) {
        return BlockCollectorCrystalBase.CollectorCrystalType.values()[NBTHelper.getPersistentData(stack)
            .getInteger("collectorType")];
    }

    public static void setConstellation(ItemStack stack, IWeakConstellation constellation) {
        constellation.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    public static IWeakConstellation getConstellation(ItemStack stack) {
        return (IWeakConstellation) IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setTraitConstellation(ItemStack stack, @Nullable IMinorConstellation constellation) {
        if (constellation == null) return;
        constellation.writeToNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey() + "trait");
    }

    public static IMinorConstellation getTrait(ItemStack stack) {
        return (IMinorConstellation) IConstellation
            .readFromNBT(NBTHelper.getPersistentData(stack), IConstellation.getDefaultSaveKey() + "trait");
    }

}
