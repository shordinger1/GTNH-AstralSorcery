package shordinger.wrapper.net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.EnumFacing;

public class TileEntityEndPortal extends TileEntity {

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderFace(EnumFacing p_184313_1_) {
        return p_184313_1_ == EnumFacing.UP;
    }
}
