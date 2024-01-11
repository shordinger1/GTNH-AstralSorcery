package shordinger.wrapper.net.minecraft.item;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.world.World;

public interface IItemPropertyGetter {

    @SideOnly(Side.CLIENT)
    float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn);
}
