package shordinger.wrapper.net.minecraft.entity;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.village.MerchantRecipe;
import shordinger.wrapper.net.minecraft.village.MerchantRecipeList;
import shordinger.wrapper.net.minecraft.world.World;

public interface IMerchant {

    void setCustomer(@Nullable EntityPlayer player);

    @Nullable
    EntityPlayer getCustomer();

    @Nullable
    MerchantRecipeList getRecipes(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    void setRecipes(@Nullable MerchantRecipeList recipeList);

    void useRecipe(MerchantRecipe recipe);

    /**
     * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
     * being played depending if the suggested itemstack is not null.
     */
    void verifySellingItem(ItemStack stack);

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    ITextComponent getDisplayName();

    World getWorld();

    BlockPos getPos();
}
