package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySpectralArrow;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemSpectralArrow extends ItemArrow {

    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        return new EntitySpectralArrow(worldIn, shooter);
    }
}
