package shordinger.wrapper.net.minecraft.world.storage.loot;

import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryEmpty extends LootEntry {

    public LootEntryEmpty(int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
        super(weightIn, qualityIn, conditionsIn, entryName);
    }

    public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
    }

    protected void serialize(JsonObject json, JsonSerializationContext context) {
    }

    public static LootEntryEmpty deserialize(JsonObject object, JsonDeserializationContext deserializationContext,
                                             int weightIn, int qualityIn, LootCondition[] conditionsIn) {
        return new LootEntryEmpty(
            weightIn,
            qualityIn,
            conditionsIn,
            net.minecraftforge.common.ForgeHooks.readLootEntryName(object, "empty"));
    }
}
