package shordinger.wrapper.net.minecraft.potion;

import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class PotionAbsorption extends Potion {

    protected PotionAbsorption(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
    }

    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn,
                                                    AbstractAttributeMap attributeMapIn, int amplifier) {
        entityLivingBaseIn
            .setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() - (float) (4 * (amplifier + 1)));
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn,
                                                 AbstractAttributeMap attributeMapIn, int amplifier) {
        entityLivingBaseIn
            .setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() + (float) (4 * (amplifier + 1)));
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }
}
