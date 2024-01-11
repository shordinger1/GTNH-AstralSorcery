package shordinger.wrapper.net.minecraft.client.renderer.block.model;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.EnumFacing;

@SideOnly(Side.CLIENT)
public class BlockPartRotation {

    public final Vector3f origin;
    public final EnumFacing.Axis axis;
    public final float angle;
    public final boolean rescale;

    public BlockPartRotation(Vector3f originIn, EnumFacing.Axis axisIn, float angleIn, boolean rescaleIn) {
        this.origin = originIn;
        this.axis = axisIn;
        this.angle = angleIn;
        this.rescale = rescaleIn;
    }
}
