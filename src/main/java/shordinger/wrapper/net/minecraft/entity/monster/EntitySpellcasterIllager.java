package shordinger.wrapper.net.minecraft.entity.monster;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.World;

public abstract class EntitySpellcasterIllager extends AbstractIllager {

    private static final DataParameter<Byte> SPELL = EntityDataManager
        .<Byte>createKey(EntitySpellcasterIllager.class, DataSerializers.BYTE);
    protected int spellTicks;
    private EntitySpellcasterIllager.SpellType activeSpell = EntitySpellcasterIllager.SpellType.NONE;

    public EntitySpellcasterIllager(World p_i47506_1_) {
        super(p_i47506_1_);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SPELL, Byte.valueOf((byte) 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.spellTicks = compound.getInteger("SpellTicks");
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("SpellTicks", this.spellTicks);
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose() {
        return this.isSpellcasting() ? AbstractIllager.IllagerArmPose.SPELLCASTING
            : AbstractIllager.IllagerArmPose.CROSSED;
    }

    public boolean isSpellcasting() {
        if (this.world.isRemote) {
            return ((Byte) this.dataManager.get(SPELL)).byteValue() > 0;
        } else {
            return this.spellTicks > 0;
        }
    }

    public void setSpellType(EntitySpellcasterIllager.SpellType spellType) {
        this.activeSpell = spellType;
        this.dataManager.set(SPELL, Byte.valueOf((byte) spellType.id));
    }

    protected EntitySpellcasterIllager.SpellType getSpellType() {
        return !this.world.isRemote ? this.activeSpell
            : EntitySpellcasterIllager.SpellType.getFromId(((Byte) this.dataManager.get(SPELL)).byteValue());
    }

    protected void updateAITasks() {
        super.updateAITasks();

        if (this.spellTicks > 0) {
            --this.spellTicks;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote && this.isSpellcasting()) {
            EntitySpellcasterIllager.SpellType entityspellcasterillager$spelltype = this.getSpellType();
            double d0 = entityspellcasterillager$spelltype.particleSpeed[0];
            double d1 = entityspellcasterillager$spelltype.particleSpeed[1];
            double d2 = entityspellcasterillager$spelltype.particleSpeed[2];
            float f = this.renderYawOffset * 0.017453292F + MathHelper.cos((float) this.ticksExisted * 0.6662F) * 0.25F;
            float f1 = MathHelper.cos(f);
            float f2 = MathHelper.sin(f);
            this.world.spawnParticle(
                EnumParticleTypes.SPELL_MOB,
                this.posX + (double) f1 * 0.6D,
                this.posY + 1.8D,
                this.posZ + (double) f2 * 0.6D,
                d0,
                d1,
                d2);
            this.world.spawnParticle(
                EnumParticleTypes.SPELL_MOB,
                this.posX - (double) f1 * 0.6D,
                this.posY + 1.8D,
                this.posZ - (double) f2 * 0.6D,
                d0,
                d1,
                d2);
        }
    }

    protected int getSpellTicks() {
        return this.spellTicks;
    }

    protected abstract SoundEvent getSpellSound();

    public class AICastingApell extends EntityAIBase {

        public AICastingApell() {
            this.setMutexBits(3);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            return EntitySpellcasterIllager.this.getSpellTicks() > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            super.startExecuting();
            EntitySpellcasterIllager.this.navigator.clearPath();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            super.resetTask();
            EntitySpellcasterIllager.this.setSpellType(EntitySpellcasterIllager.SpellType.NONE);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask() {
            if (EntitySpellcasterIllager.this.getAttackTarget() != null) {
                EntitySpellcasterIllager.this.getLookHelper()
                    .setLookPositionWithEntity(
                        EntitySpellcasterIllager.this.getAttackTarget(),
                        (float) EntitySpellcasterIllager.this.getHorizontalFaceSpeed(),
                        (float) EntitySpellcasterIllager.this.getVerticalFaceSpeed());
            }
        }
    }

    public abstract class AIUseSpell extends EntityAIBase {

        protected int spellWarmup;
        protected int spellCooldown;

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (EntitySpellcasterIllager.this.getAttackTarget() == null) {
                return false;
            } else if (EntitySpellcasterIllager.this.isSpellcasting()) {
                return false;
            } else {
                return EntitySpellcasterIllager.this.ticksExisted >= this.spellCooldown;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EntitySpellcasterIllager.this.getAttackTarget() != null && this.spellWarmup > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.spellWarmup = this.getCastWarmupTime();
            EntitySpellcasterIllager.this.spellTicks = this.getCastingTime();
            this.spellCooldown = EntitySpellcasterIllager.this.ticksExisted + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();

            if (soundevent != null) {
                EntitySpellcasterIllager.this.playSound(soundevent, 1.0F, 1.0F);
            }

            EntitySpellcasterIllager.this.setSpellType(this.getSpellType());
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask() {
            --this.spellWarmup;

            if (this.spellWarmup == 0) {
                this.castSpell();
                EntitySpellcasterIllager.this.playSound(EntitySpellcasterIllager.this.getSpellSound(), 1.0F, 1.0F);
            }
        }

        protected abstract void castSpell();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract EntitySpellcasterIllager.SpellType getSpellType();
    }

    public static enum SpellType {

        NONE(0, 0.0D, 0.0D, 0.0D),
        SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
        FANGS(2, 0.4D, 0.3D, 0.35D),
        WOLOLO(3, 0.7D, 0.5D, 0.2D),
        DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
        BLINDNESS(5, 0.1D, 0.1D, 0.2D);

        private final int id;
        /**
         * Particle motion speed. An array with 3 values: x, y, and z.
         */
        private final double[] particleSpeed;

        private SpellType(int idIn, double xParticleSpeed, double yParticleSpeed, double zParticleSpeed) {
            this.id = idIn;
            this.particleSpeed = new double[]{xParticleSpeed, yParticleSpeed, zParticleSpeed};
        }

        public static EntitySpellcasterIllager.SpellType getFromId(int idIn) {
            for (EntitySpellcasterIllager.SpellType entityspellcasterillager$spelltype : values()) {
                if (idIn == entityspellcasterillager$spelltype.id) {
                    return entityspellcasterillager$spelltype;
                }
            }

            return NONE;
        }
    }
}
