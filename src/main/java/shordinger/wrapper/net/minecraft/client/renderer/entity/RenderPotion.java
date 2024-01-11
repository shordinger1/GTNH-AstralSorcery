package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.RenderItem;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityPotion;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class RenderPotion extends RenderSnowball<EntityPotion> {

    public RenderPotion(RenderManager renderManagerIn, RenderItem itemRendererIn) {
        super(renderManagerIn, Items.POTIONITEM, itemRendererIn);
    }

    public ItemStack getStackToRender(EntityPotion entityIn) {
        return entityIn.getPotion();
    }
}
