package shordinger.wrapper.net.minecraft.client.renderer.culling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;

@SideOnly(Side.CLIENT)
public interface ICamera {

    /**
     * Returns true if the bounding box is inside all 6 clipping planes, otherwise returns false.
     */
    boolean isBoundingBoxInFrustum(AxisAlignedBB p_78546_1_);

    void setPosition(double xIn, double yIn, double zIn);
}
