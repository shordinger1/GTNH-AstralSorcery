/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.advancements.critereon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;

import shordinger.wrapper.net.minecraft.advancements.critereon.ItemPredicate;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

public class ItemPredicates {

    private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> predicates = new HashMap<>();

    static {
        register(new ResourceLocation("forge:ore_dict"), OredictItemPredicate::new);
    }

    public static void register(ResourceLocation rl, Function<JsonObject, ItemPredicate> jsonToPredicate) {
        predicates.put(rl, jsonToPredicate);
    }

    public static Map<ResourceLocation, Function<JsonObject, ItemPredicate>> getPredicates() {
        return Collections.unmodifiableMap(predicates);
    }
}
