package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.EnumDifficulty;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(World worldIn) {
        super(worldIn);
        this.isImmuneToFire = true;
    }

    public static void registerFixesMagmaCube(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityMagmaCube.class);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.20000000298023224D);
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    /**
     * Checks that the entity is not colliding with any blocks / liquids
     */
    public boolean isNotColliding() {
        return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this)
            && this.world.getCollisionBoxes(this, this.getEntityBoundingBox())
            .isEmpty()
            && !this.world.containsAnyLiquid(this.getEntityBoundingBox());
    }

    protected void setSlimeSize(int size, boolean resetHealth) {
        super.setSlimeSize(size, resetHealth);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR)
            .setBaseValue((double) (size * 3));
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness() {
        return 1.0F;
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.FLAME;
    }

    protected EntitySlime createInstance() {
        return new EntityMagmaCube(this.world);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return this.isSmallSlime() ? LootTableList.EMPTY : LootTableList.ENTITIES_MAGMA_CUBE;
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning() {
        return false;
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int getJumpDelay() {
        return super.getJumpDelay() * 4;
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.9F;
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump() {
        this.motionY = (double) (0.42F + (float) this.getSlimeSize() * 0.1F);
        this.isAirBorne = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(this);
    }

    protected void handleJumpLava() {
        this.motionY = (double) (0.22F + (float) this.getSlimeSize() * 0.05F);
        this.isAirBorne = true;
    }

    public void fall(float distance, float damageMultiplier) {
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the slime's size)
     */
    protected boolean canDamagePlayer() {
        return true;
    }

    /**
     * Gets the amount of damage dealt to the player when "attacked" by the slime.
     */
    protected int getAttackStrength() {
        return super.getAttackStrength() + 2;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_MAGMACUBE_HURT : SoundEvents.ENTITY_MAGMACUBE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_MAGMACUBE_DEATH : SoundEvents.ENTITY_MAGMACUBE_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return this.isSmallSlime() ? SoundEvents.ENTITY_SMALL_MAGMACUBE_SQUISH : SoundEvents.ENTITY_MAGMACUBE_SQUISH;
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_MAGMACUBE_JUMP;
    }
}
