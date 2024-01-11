package shordinger.wrapper.net.minecraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartMobSpawner;

@SideOnly(Side.CLIENT)
public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner> {

    public RenderMinecartMobSpawner(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }
}
