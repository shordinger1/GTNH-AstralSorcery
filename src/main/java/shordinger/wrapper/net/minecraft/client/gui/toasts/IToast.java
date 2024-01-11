package shordinger.wrapper.net.minecraft.client.gui.toasts;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.audio.PositionedSoundRecord;
import shordinger.wrapper.net.minecraft.client.audio.SoundHandler;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;

@SideOnly(Side.CLIENT)
public interface IToast {

    ResourceLocation TEXTURE_TOASTS = new ResourceLocation("textures/gui/toasts.png");
    Object NO_TOKEN = new Object();

    IToast.Visibility draw(GuiToast toastGui, long delta);

    default Object getType() {
        return NO_TOKEN;
    }

    @SideOnly(Side.CLIENT)
    public static enum Visibility {

        SHOW(SoundEvents.UI_TOAST_IN),
        HIDE(SoundEvents.UI_TOAST_OUT);

        private final SoundEvent sound;

        private Visibility(SoundEvent soundIn) {
            this.sound = soundIn;
        }

        public void playSound(SoundHandler handler) {
            handler.playSound(PositionedSoundRecord.getRecord(this.sound, 1.0F, 1.0F));
        }
    }
}
