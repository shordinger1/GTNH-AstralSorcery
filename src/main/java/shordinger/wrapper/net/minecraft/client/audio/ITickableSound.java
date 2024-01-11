package shordinger.wrapper.net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.ITickable;

@SideOnly(Side.CLIENT)
public interface ITickableSound extends ISound, ITickable {

    boolean isDonePlaying();
}
