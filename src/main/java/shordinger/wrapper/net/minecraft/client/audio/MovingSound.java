package shordinger.wrapper.net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.SoundEvent;

@SideOnly(Side.CLIENT)
public abstract class MovingSound extends PositionedSound implements ITickableSound {

    protected boolean donePlaying;

    protected MovingSound(SoundEvent soundIn, SoundCategory categoryIn) {
        super(soundIn, categoryIn);
    }

    public boolean isDonePlaying() {
        return this.donePlaying;
    }
}
