package shordinger.wrapper.net.minecraft.world.storage.loot;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.JsonUtils;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.storage.loot.conditions.LootCondition;
import shordinger.wrapper.net.minecraft.world.storage.loot.conditions.LootConditionManager;

public class LootPool {

    private final List<LootEntry> lootEntries;
    private final List<LootCondition> poolConditions;
    private RandomValueRange rolls;
    private RandomValueRange bonusRolls;
    private final String name;

    public LootPool(LootEntry[] lootEntriesIn, LootCondition[] poolConditionsIn, RandomValueRange rollsIn,
                    RandomValueRange bonusRollsIn, String name) {
        this.lootEntries = Lists.newArrayList(lootEntriesIn);
        this.poolConditions = Lists.newArrayList(poolConditionsIn);
        this.rolls = rollsIn;
        this.bonusRolls = bonusRollsIn;
        this.name = name;
    }

    /**
     * generates the contents for a single roll.
     * The first for loop calculates the sum of all the lootentries
     * and the second for loop adds a random item
     * with items with higher weights being more probable.
     */
    protected void createLootRoll(Collection<ItemStack> stacks, Random rand, LootContext context) {
        List<LootEntry> list = Lists.<LootEntry>newArrayList();
        int i = 0;

        for (LootEntry lootentry : this.lootEntries) {
            if (LootConditionManager.testAllConditions(lootentry.conditions, rand, context)) {
                int j = lootentry.getEffectiveWeight(context.getLuck());

                if (j > 0) {
                    list.add(lootentry);
                    i += j;
                }
            }
        }

        if (i != 0 && !list.isEmpty()) {
            int k = rand.nextInt(i);

            for (LootEntry lootentry1 : list) {
                k -= lootentry1.getEffectiveWeight(context.getLuck());

                if (k < 0) {
                    lootentry1.addLoot(stacks, rand, context);
                    return;
                }
            }
        }
    }

    /**
     * generates loot and puts it in an inventory
     */
    public void generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
        if (LootConditionManager.testAllConditions(this.poolConditions, rand, context)) {
            int i = this.rolls.generateInt(rand)
                + MathHelper.floor(this.bonusRolls.generateFloat(rand) * context.getLuck());

            for (int j = 0; j < i; ++j) {
                this.createLootRoll(stacks, rand, context);
            }
        }
    }

    // ======================== FORGE START =============================================
    private boolean isFrozen = false;

    public void freeze() {
        this.isFrozen = true;
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    private void checkFrozen() {
        if (this.isFrozen()) throw new RuntimeException("Attempted to modify LootPool after being frozen!");
    }

    public String getName() {
        return this.name;
    }

    public RandomValueRange getRolls() {
        return this.rolls;
    }

    public RandomValueRange getBonusRolls() {
        return this.bonusRolls;
    }

    public void setRolls(RandomValueRange v) {
        checkFrozen();
        this.rolls = v;
    }

    public void setBonusRolls(RandomValueRange v) {
        checkFrozen();
        this.bonusRolls = v;
    }

    public LootEntry getEntry(String name) {
        for (LootEntry entry : this.lootEntries) if (name.equals(entry.getEntryName())) return entry;
        return null;
    }

    public LootEntry removeEntry(String name) {
        checkFrozen();
        for (LootEntry entry : this.lootEntries) {
            if (name.equals(entry.getEntryName())) {
                this.lootEntries.remove(entry);
                return entry;
            }
        }
        return null;
    }

    public void addEntry(LootEntry entry) {
        checkFrozen();
        for (LootEntry e : this.lootEntries)
            if (e == entry || e.getEntryName()
                .equals(entry.getEntryName()))
                throw new RuntimeException("Attempted to add a duplicate entry to pool: " + e.getEntryName());
        this.lootEntries.add(entry);
    }
    // TODO: Allow modifications of conditions? If so need a way to uniquely identify them.
    // ======================== FORGE END ===============================================

    public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {

        public LootPool deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_,
                                    JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "loot pool");
            String name = net.minecraftforge.common.ForgeHooks.readPoolName(jsonobject);
            LootEntry[] alootentry = (LootEntry[]) JsonUtils
                .deserializeClass(jsonobject, "entries", p_deserialize_3_, LootEntry[].class);
            LootCondition[] alootcondition = (LootCondition[]) JsonUtils.deserializeClass(
                jsonobject,
                "conditions",
                new LootCondition[0],
                p_deserialize_3_,
                LootCondition[].class);
            RandomValueRange randomvaluerange = (RandomValueRange) JsonUtils
                .deserializeClass(jsonobject, "rolls", p_deserialize_3_, RandomValueRange.class);
            RandomValueRange randomvaluerange1 = (RandomValueRange) JsonUtils.deserializeClass(
                jsonobject,
                "bonus_rolls",
                new RandomValueRange(0.0F, 0.0F),
                p_deserialize_3_,
                RandomValueRange.class);
            return new LootPool(alootentry, alootcondition, randomvaluerange, randomvaluerange1, name);
        }

        public JsonElement serialize(LootPool p_serialize_1_, Type p_serialize_2_,
                                     JsonSerializationContext p_serialize_3_) {
            JsonObject jsonobject = new JsonObject();
            if (p_serialize_1_.name != null && !p_serialize_1_.name.startsWith("custom#"))
                jsonobject.add("name", p_serialize_3_.serialize(p_serialize_1_.name));
            jsonobject.add("entries", p_serialize_3_.serialize(p_serialize_1_.lootEntries));
            jsonobject.add("rolls", p_serialize_3_.serialize(p_serialize_1_.rolls));

            if (p_serialize_1_.bonusRolls.getMin() != 0.0F && p_serialize_1_.bonusRolls.getMax() != 0.0F) {
                jsonobject.add("bonus_rolls", p_serialize_3_.serialize(p_serialize_1_.bonusRolls));
            }

            if (!p_serialize_1_.poolConditions.isEmpty()) {
                jsonobject.add("conditions", p_serialize_3_.serialize(p_serialize_1_.poolConditions));
            }

            return jsonobject;
        }
    }
}
