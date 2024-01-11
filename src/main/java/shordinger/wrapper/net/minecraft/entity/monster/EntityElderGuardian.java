package shordinger.wrapper.net.minecraft.entity.monster;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.network.play.server.SPacketChangeGameState;
import shordinger.wrapper.net.minecraft.potion.Potion;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityElderGuardian extends EntityGuardian {

    public EntityElderGuardian(World worldIn) {
        super(worldIn);
        this.setSize(this.width * 2.35F, this.height * 2.35F);
        this.enablePersistence();

        if (this.wander != null) {
            this.wander.setExecutionChance(400);
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.30000001192092896D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
            .setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(80.0D);
    }

    public static void registerFixesElderGuardian(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityElderGuardian.class);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_ELDER_GUARDIAN;
    }

    public int getAttackDuration() {
        return 60;
    }

    @SideOnly(Side.CLIENT)
    public void setGhost() {
        this.clientSideSpikesAnimation = 1.0F;
        this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
    }

    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT
            : SoundEvents.ENTITY_ELDERGUARDIAN_AMBIENTLAND;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_HURT : SoundEvents.ENTITY_ELDER_GUARDIAN_HURT_LAND;
    }

    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH
            : SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH_LAND;
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_FLOP;
    }

    protected void updateAITasks() {
        super.updateAITasks();
        int i = 1200;

        if ((this.ticksExisted + this.getEntityId()) % 1200 == 0) {
            Potion potion = MobEffects.MINING_FATIGUE;
            List<EntityPlayerMP> list = this.world
                .<EntityPlayerMP>getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {

                    public boolean apply(@Nullable EntityPlayerMP p_apply_1_) {
                        return EntityElderGuardian.this.getDistanceSq(p_apply_1_) < 2500.0D
                            && p_apply_1_.interactionManager.survivalOrAdventure();
                    }
                });
            int j = 2;
            int k = 6000;
            int l = 1200;

            for (EntityPlayerMP entityplayermp : list) {
                if (!entityplayermp.isPotionActive(potion) || entityplayermp.getActivePotionEffect(potion)
                    .getAmplifier() < 2
                    || entityplayermp.getActivePotionEffect(potion)
                    .getDuration() < 1200) {
                    entityplayermp.connection.sendPacket(new SPacketChangeGameState(10, 0.0F));
                    entityplayermp.addPotionEffect(new PotionEffect(potion, 6000, 2));
                }
            }
        }

        if (!this.hasHome()) {
            this.setHomePosAndDistance(new BlockPos(this), 16);
        }
    }
}
