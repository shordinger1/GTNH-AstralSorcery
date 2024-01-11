package shordinger.wrapper.net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IJumpingMount {

    @SideOnly(Side.CLIENT)
    void setJumpPower(int jumpPowerIn);

    boolean canJump();

    void handleStartJump(int p_184775_1_);

    void handleStopJump();
}
