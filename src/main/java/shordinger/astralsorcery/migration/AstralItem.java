package shordinger.astralsorcery.migration;

import static shordinger.astralsorcery.migration.ITooltipFlag.TooltipFlags.trans;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AstralItem extends Item {

    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List p_150895_3_) {
        getSubItems(p_150895_2_, p_150895_3_);
    }

    public abstract void getSubItems(CreativeTabs tab, List<ItemStack> items);

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean flag) {
        addInformation(stack, player.getEntityWorld(), tooltip, trans(flag));
    }

    @SideOnly(Side.CLIENT)
    public abstract void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                        ITooltipFlag flagIn);

    @Override
    public final ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        ActionResult<ItemStack> result = onItemRightClick(worldIn, player);
        return result.getResult();
    }

    // @Override
    public abstract ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player);

    // TODO ???
    // @Override
    // public boolean needsSpecialHandling(World world, BlockPos at, EntityPlayer player, ItemStack stack) {
    // }

    public abstract boolean onRightClick(World world, BlockPos pos, EntityPlayer player, EnumFacing side,
                                         ItemStack stack);

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        try {
            onUpdate(entityItem.getEntityItem(), entityItem.worldObj, entityItem.ridingEntity, 1, false);
        } catch (Exception ignore) {
            return false;
        }
        return super.onEntityItemUpdate(entityItem);
    }

    public abstract void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected);

    public abstract ModelResourceLocation getModelLocation(ItemStack stack, ModelResourceLocation defaultModelPath);

    public abstract List<ResourceLocation> getAllPossibleLocations(ModelResourceLocation defaultLocation);
}

interface ITooltipFlag {

    boolean isAdvanced();

    @SideOnly(Side.CLIENT)
    public static enum TooltipFlags implements ITooltipFlag {

        NORMAL(false),
        ADVANCED(true);

        final boolean isAdvanced;

        private TooltipFlags(boolean advanced) {
            this.isAdvanced = advanced;
        }

        public boolean isAdvanced() {
            return this.isAdvanced;
        }

        public static TooltipFlags trans(boolean flag) {
            return flag ? ADVANCED : NORMAL;
        }
    }
}
