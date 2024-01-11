package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.crafting.CraftingManager;
import shordinger.wrapper.net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemKnowledgeBook extends Item {

    private static final Logger LOGGER = LogManager.getLogger();

    public ItemKnowledgeBook() {
        this.setMaxStackSize(1);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        NBTTagCompound nbttagcompound = itemstack.getTagCompound();

        if (!playerIn.capabilities.isCreativeMode) {
            playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        }

        if (nbttagcompound != null && nbttagcompound.hasKey("Recipes", 9)) {
            if (!worldIn.isRemote) {
                NBTTagList nbttaglist = nbttagcompound.getTagList("Recipes", 8);
                List<IRecipe> list = Lists.<IRecipe>newArrayList();

                for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                    String s = nbttaglist.getStringTagAt(i);
                    IRecipe irecipe = CraftingManager.getRecipe(new ResourceLocation(s));

                    if (irecipe == null) {
                        LOGGER.error("Invalid recipe: " + s);
                        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
                    }

                    list.add(irecipe);
                }

                playerIn.unlockRecipes(list);
                playerIn.addStat(StatList.getObjectUseStats(this));
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            LOGGER.error("Tag not valid: " + nbttagcompound);
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }
}
