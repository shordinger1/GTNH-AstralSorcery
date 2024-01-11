package shordinger.wrapper.net.minecraft.world.storage.loot.functions;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.JsonUtils;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootContext;
import shordinger.wrapper.net.minecraft.world.storage.loot.RandomValueRange;
import shordinger.wrapper.net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetCount extends LootFunction {

    private final RandomValueRange countRange;

    public SetCount(LootCondition[] conditionsIn, RandomValueRange countRangeIn) {
        super(conditionsIn);
        this.countRange = countRangeIn;
    }

    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        stack.setCount(this.countRange.generateInt(rand));
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetCount> {

        protected Serializer() {
            super(new ResourceLocation("set_count"), SetCount.class);
        }

        public void serialize(JsonObject object, SetCount functionClazz,
                              JsonSerializationContext serializationContext) {
            object.add("count", serializationContext.serialize(functionClazz.countRange));
        }

        public SetCount deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                    LootCondition[] conditionsIn) {
            return new SetCount(
                conditionsIn,
                (RandomValueRange) JsonUtils
                    .deserializeClass(object, "count", deserializationContext, RandomValueRange.class));
        }
    }
}
