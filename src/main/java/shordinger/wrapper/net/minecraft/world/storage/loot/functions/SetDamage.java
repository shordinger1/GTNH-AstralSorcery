package shordinger.wrapper.net.minecraft.world.storage.loot.functions;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.JsonUtils;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootContext;
import shordinger.wrapper.net.minecraft.world.storage.loot.RandomValueRange;
import shordinger.wrapper.net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetDamage extends LootFunction {

    private static final Logger LOGGER = LogManager.getLogger();
    private final RandomValueRange damageRange;

    public SetDamage(LootCondition[] conditionsIn, RandomValueRange damageRangeIn) {
        super(conditionsIn);
        this.damageRange = damageRangeIn;
    }

    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        if (stack.isItemStackDamageable()) {
            float f = 1.0F - this.damageRange.generateFloat(rand);
            stack.setItemDamage(MathHelper.floor(f * (float) stack.getMaxDamage()));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object) stack);
        }

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetDamage> {

        protected Serializer() {
            super(new ResourceLocation("set_damage"), SetDamage.class);
        }

        public void serialize(JsonObject object, SetDamage functionClazz,
                              JsonSerializationContext serializationContext) {
            object.add("damage", serializationContext.serialize(functionClazz.damageRange));
        }

        public SetDamage deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                     LootCondition[] conditionsIn) {
            return new SetDamage(
                conditionsIn,
                (RandomValueRange) JsonUtils
                    .deserializeClass(object, "damage", deserializationContext, RandomValueRange.class));
        }
    }
}