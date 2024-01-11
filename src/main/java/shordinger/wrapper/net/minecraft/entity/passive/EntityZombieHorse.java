package shordinger.wrapper.net.minecraft.entity.passive;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.EnumCreatureAttribute;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityZombieHorse extends AbstractHorse {

    public EntityZombieHorse(World worldIn) {
        super(worldIn);
    }

    public static void registerFixesZombieHorse(DataFixer fixer) {
        AbstractHorse.registerFixesAbstractHorse(fixer, EntityZombieHorse.class);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(15.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.20000000298023224D);
        this.getEntityAttribute(JUMP_STRENGTH)
            .setBaseValue(this.getModifiedJumpStrength());
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_ZOMBIE_HORSE;
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() == Items.SPAWN_EGG) {
            return super.processInteract(player, hand);
        } else if (!this.isTame()) {
            return false;
        } else if (this.isChild()) {
            return super.processInteract(player, hand);
        } else if (player.isSneaking()) {
            this.openGUI(player);
            return true;
        } else if (this.isBeingRidden()) {
            return super.processInteract(player, hand);
        } else {
            if (flag) {
                if (!this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE) {
                    this.openGUI(player);
                    return true;
                }

                if (itemstack.interactWithEntity(player, this, hand)) {
                    return true;
                }
            }

            this.mountTo(player);
            return true;
        }
    }
}
