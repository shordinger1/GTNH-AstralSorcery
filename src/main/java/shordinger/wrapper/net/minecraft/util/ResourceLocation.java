package shordinger.wrapper.net.minecraft.util;

import java.lang.reflect.Type;
import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResourceLocation extends net.minecraft.util.ResourceLocation implements Comparable<ResourceLocation> {

    protected ResourceLocation(int unused, String... resourceName) {

        super(
            org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft"
                : resourceName[0].toLowerCase(Locale.ROOT),
            resourceName[1].toLowerCase(Locale.ROOT));
    }

    public ResourceLocation(String resourceName) {
        this(0, splitObjectName(resourceName));
    }

    public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
        this(0, resourceDomainIn, resourcePathIn);
    }

    /**
     * Splits an object name (such as minecraft:apple) into the domain and path parts and returns these as an array of
     * length 2. If no colon is present in the passed value the returned array will contain {null, toSplit}.
     */
    public static String[] splitObjectName(String toSplit) {
        String[] astring = new String[]{"minecraft", toSplit};
        int i = toSplit.indexOf(58);

        if (i >= 0) {
            astring[1] = toSplit.substring(i + 1, toSplit.length());

            if (i > 1) {
                astring[0] = toSplit.substring(0, i);
            }
        }

        return astring;
    }

    public int compareTo(ResourceLocation p_compareTo_1_) {
        int i = getResourceDomain().compareTo(p_compareTo_1_.getResourceDomain());

        if (i == 0) {
            i = getResourcePath().compareTo(p_compareTo_1_.getResourcePath());
        }

        return i;
    }

    public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {

        public ResourceLocation deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_,
                                            JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            return new ResourceLocation(JsonUtils.getString(p_deserialize_1_, "location"));
        }

        public JsonElement serialize(ResourceLocation p_serialize_1_, Type p_serialize_2_,
                                     JsonSerializationContext p_serialize_3_) {
            return new JsonPrimitive(p_serialize_1_.toString());
        }
    }
}
