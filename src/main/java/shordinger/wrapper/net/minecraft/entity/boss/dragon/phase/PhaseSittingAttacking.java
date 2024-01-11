package shordinger.wrapper.net.minecraft.entity.boss.dragon.phase;

import shordinger.wrapper.net.minecraft.entity.boss.EntityDragon;
import shordinger.wrapper.net.minecraft.init.SoundEvents;

public class PhaseSittingAttacking extends PhaseSittingBase {

    private int attackingTicks;

    public PhaseSittingAttacking(EntityDragon dragonIn) {
        super(dragonIn);
    }

    /**
     * Generates particle effects appropriate to the phase (or sometimes sounds).
     * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
     */
    public void doClientRenderEffects() {
        this.dragon.world.playSound(
            this.dragon.posX,
            this.dragon.posY,
            this.dragon.posZ,
            SoundEvents.ENTITY_ENDERDRAGON_GROWL,
            this.dragon.getSoundCategory(),
            2.5F,
            0.8F + this.dragon.getRNG()
                .nextFloat() * 0.3F,
            false);
    }

    /**
     * Gives the phase a chance to update its status.
     * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
     */
    public void doLocalUpdate() {
        if (this.attackingTicks++ >= 40) {
            this.dragon.getPhaseManager()
                .setPhase(PhaseList.SITTING_FLAMING);
        }
    }

    /**
     * Called when this phase is set to active
     */
    public void initPhase() {
        this.attackingTicks = 0;
    }

    public PhaseList<PhaseSittingAttacking> getType() {
        return PhaseList.SITTING_ATTACKING;
    }
}
