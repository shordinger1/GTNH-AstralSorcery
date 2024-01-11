package shordinger.wrapper.net.minecraft.potion;

import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class PotionHealthBoost extends Potion {

    public PotionHealthBoost(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn,
                                                    AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);

        if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()) {
            entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
        }
    }
}
