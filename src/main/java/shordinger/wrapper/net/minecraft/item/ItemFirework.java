package shordinger.wrapper.net.minecraft.item;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.util.ITooltipFlag;
import shordinger.wrapper.net.minecraft.entity.item.EntityFireworkRocket;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemFirework extends Item {

    /**
     * Called when a Block is right-clicked with this Item
     */
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack itemstack = player.getHeldItem(hand);
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(
                worldIn,
                (double) ((float) pos.getX() + hitX),
                (double) ((float) pos.getY() + hitY),
                (double) ((float) pos.getZ() + hitZ),
                itemstack);
            worldIn.spawnEntity(entityfireworkrocket);

            if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }
        }

        return EnumActionResult.SUCCESS;
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.isElytraFlying()) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);

            if (!worldIn.isRemote) {
                EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(worldIn, itemstack, playerIn);
                worldIn.spawnEntity(entityfireworkrocket);

                if (!playerIn.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        } else {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound nbttagcompound = stack.getSubCompound("Fireworks");

        if (nbttagcompound != null) {
            if (nbttagcompound.hasKey("Flight", 99)) {
                tooltip.add(I18n.translateToLocal("item.fireworks.flight") + " " + nbttagcompound.getByte("Flight"));
            }

            NBTTagList nbttaglist = nbttagcompound.getTagList("Explosions", 10);

            if (!nbttaglist.hasNoTags()) {
                for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                    NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                    List<String> list = Lists.<String>newArrayList();
                    ItemFireworkCharge.addExplosionInfo(nbttagcompound1, list);

                    if (!list.isEmpty()) {
                        for (int j = 1; j < list.size(); ++j) {
                            list.set(j, "  " + (String) list.get(j));
                        }

                        tooltip.addAll(list);
                    }
                }
            }
        }
    }
}
