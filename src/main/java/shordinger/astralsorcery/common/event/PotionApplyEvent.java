/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event;

import shordinger.astralsorcery.core.ASMCallHook;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.event.entity.living.LivingEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionApplyEvent
 * Created by HellFirePvP
 * Date: 24.10.2018 / 21:25
 */
public class PotionApplyEvent {

    public static class New extends LivingEvent {

        private final PotionEffect applied;

        public New(EntityLivingBase entity, PotionEffect applied) {
            super(entity);
            this.applied = applied;
        }

        public PotionEffect getPotionEffect() {
            return applied;
        }
    }

    public static class Changed extends LivingEvent {

        private final PotionEffect addedEffect, newCombinedEffect;

        public Changed(EntityLivingBase entity, PotionEffect newlyAddedEffect, PotionEffect newCombinedEffect) {
            super(entity);
            this.addedEffect = newlyAddedEffect;
            this.newCombinedEffect = newCombinedEffect;
        }

        public PotionEffect getAddedEffect() {
            return addedEffect;
        }

        public PotionEffect getNewCombinedEffect() {
            return newCombinedEffect;
        }
    }

    @ASMCallHook
    public static void fireNew(EntityLivingBase entity, PotionEffect added) {
        MinecraftForge.EVENT_BUS.post(new New(entity, added));
    }

    @ASMCallHook
    public static void fireChanged(EntityLivingBase entity, PotionEffect previous, PotionEffect newCombined) {
        MinecraftForge.EVENT_BUS.post(new Changed(entity, previous, newCombined));
    }

}
