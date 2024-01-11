package shordinger.wrapper.net.minecraft.client.audio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecart;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class MovingSoundMinecartRiding extends MovingSound {

    private final EntityPlayer player;
    private final EntityMinecart minecart;

    public MovingSoundMinecartRiding(EntityPlayer playerRiding, EntityMinecart minecart) {
        super(SoundEvents.ENTITY_MINECART_INSIDE, SoundCategory.NEUTRAL);
        this.player = playerRiding;
        this.minecart = minecart;
        this.attenuationType = ISound.AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        if (!this.minecart.isDead && this.player.isRiding() && this.player.getRidingEntity() == this.minecart) {
            float f = MathHelper
                .sqrt(this.minecart.motionX * this.minecart.motionX + this.minecart.motionZ * this.minecart.motionZ);

            if ((double) f >= 0.01D) {
                this.volume = 0.0F + MathHelper.clamp(f, 0.0F, 1.0F) * 0.75F;
            } else {
                this.volume = 0.0F;
            }
        } else {
            this.donePlaying = true;
        }
    }
}
