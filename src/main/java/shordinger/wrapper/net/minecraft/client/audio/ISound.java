package shordinger.wrapper.net.minecraft.client.audio;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundCategory;

@SideOnly(Side.CLIENT)
public interface ISound {

    ResourceLocation getSoundLocation();

    @Nullable
    SoundEventAccessor createAccessor(SoundHandler handler);

    Sound getSound();

    SoundCategory getCategory();

    boolean canRepeat();

    int getRepeatDelay();

    float getVolume();

    float getPitch();

    float getXPosF();

    float getYPosF();

    float getZPosF();

    ISound.AttenuationType getAttenuationType();

    @SideOnly(Side.CLIENT)
    public static enum AttenuationType {

        NONE(0),
        LINEAR(2);

        private final int type;

        private AttenuationType(int typeIn) {
            this.type = typeIn;
        }

        public int getTypeInt() {
            return this.type;
        }
    }
}
