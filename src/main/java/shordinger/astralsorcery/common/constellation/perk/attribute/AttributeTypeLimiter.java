/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.migration.MathHelper;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeLimiter
 * Created by HellFirePvP
 * Date: 20.11.2018 / 14:56
 */
public class AttributeTypeLimiter {

    public static final AttributeTypeLimiter INSTANCE = new AttributeTypeLimiter();

    private static final Tuple<Float, Float> NONE = new Tuple<>(null, null);
    private static final Tuple<Float, Float> ANY = new Tuple<>(Float.MIN_VALUE, Float.MAX_VALUE);
    private static Map<PerkAttributeType, Tuple<Float, Float>> perkTypeLimits = Maps.newHashMap();

    private AttributeTypeLimiter() {
    }

    void putLimit(PerkAttributeType type, float lower, float upper) {
        perkTypeLimits.put(type, new Tuple<>(lower, upper));
    }

    @Nullable
    public Float getMaxLimit(PerkAttributeType type) {
        return perkTypeLimits.getOrDefault(type, NONE).value;
    }

    @SubscribeEvent
    public void onProcess(AttributeEvent.PostProcessVanilla ev) {
        PerkAttributeType type = ev.resolveAttributeType();
        if (type != null) { // If managed
            checkValue(type, (float) ev.getValue(), ev::setValue);
        }
    }

    @SubscribeEvent
    public void onProcess(AttributeEvent.PostProcessModded ev) {
        checkValue(ev.getType(), (float) ev.getValue(), ev::setValue);
    }

    private void checkValue(PerkAttributeType type, float value, Consumer<Float> setValue) {
        Tuple<Float, Float> limit = perkTypeLimits.getOrDefault(type, ANY);
        setValue.accept(MathHelper.clamp(value, limit.key, limit.value));
    }

}
