package shordinger.wrapper.net.minecraft.util;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.util.text.translation.I18n;

public class EntityDamageSource extends DamageSource {

    @Nullable
    protected Entity damageSourceEntity;
    /**
     * Whether this EntityDamageSource is from an entity wearing Thorns-enchanted armor.
     */
    private boolean isThornsDamage;

    public EntityDamageSource(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
        super(damageTypeIn);
        this.damageSourceEntity = damageSourceEntityIn;
    }

    /**
     * Sets this EntityDamageSource as originating from Thorns armor
     */
    public EntityDamageSource setIsThornsDamage() {
        this.isThornsDamage = true;
        return this;
    }

    public boolean getIsThornsDamage() {
        return this.isThornsDamage;
    }

    /**
     * Retrieves the true causer of the damage, e.g. the player who fired an arrow, the shulker who fired the bullet,
     * etc.
     */
    @Nullable
    public Entity getTrueSource() {
        return this.damageSourceEntity;
    }

    /**
     * Gets the death message that is displayed when the player dies
     */
    public ITextComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
        ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase
            ? ((EntityLivingBase) this.damageSourceEntity).getHeldItemMainhand()
            : ItemStack.EMPTY;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return !itemstack.isEmpty() && itemstack.hasDisplayName() && I18n.canTranslate(s1)
            ? new TextComponentTranslation(
            s1,
            new Object[]{entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName(),
                itemstack.getTextComponent()})
            : new TextComponentTranslation(
            s,
            new Object[]{entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName()});
    }

    /**
     * Return whether this damage source will have its damage amount scaled based on the current difficulty.
     */
    public boolean isDifficultyScaled() {
        return this.damageSourceEntity != null && this.damageSourceEntity instanceof EntityLivingBase
            && !(this.damageSourceEntity instanceof EntityPlayer);
    }

    /**
     * Gets the location from which the damage originates.
     */
    @Nullable
    public Vec3d getDamageLocation() {
        return new Vec3d(this.damageSourceEntity.posX, this.damageSourceEntity.posY, this.damageSourceEntity.posZ);
    }
}
