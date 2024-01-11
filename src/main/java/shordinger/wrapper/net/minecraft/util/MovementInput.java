package shordinger.wrapper.net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.math.Vec2f;

@SideOnly(Side.CLIENT)
public class MovementInput {

    /**
     * The speed at which the player is strafing. Postive numbers to the left and negative to the right.
     */
    public float moveStrafe;
    public float moveForward;
    public boolean forwardKeyDown;
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneak;

    public void updatePlayerMoveState() {
    }

    public Vec2f getMoveVector() {
        return new Vec2f(this.moveStrafe, this.moveForward);
    }
}
