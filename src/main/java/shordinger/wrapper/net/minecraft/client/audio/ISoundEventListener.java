package shordinger.wrapper.net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISoundEventListener {

    void soundPlay(ISound soundIn, SoundEventAccessor accessor);
}
