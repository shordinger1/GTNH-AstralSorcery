/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.PerkConverter;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeModifier
 * Created by HellFirePvP
 * Date: 08.07.2018 / 11:08
 */
public class PerkAttributeModifier {

    private static long counter = 0;

    private long id;
    protected final Mode mode;
    protected final String attributeType;
    protected float value;

    // Cannot be converted to anything else.
    private boolean absolute = false;

    // Cached in case the value of the modifier actually is supposed to change down the road.
    protected double ctMultiplier = 1.0D;

    private Map<PerkConverter, Table<String, Mode, PerkAttributeModifier>> cachedConverters = Maps.newHashMap();

    public PerkAttributeModifier(String type, Mode mode, float value) {
        this.id = counter;
        counter++;
        this.attributeType = type;
        this.mode = mode;
        this.value = value;
        initModifier();
    }

    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    protected void initModifier() {}

    protected void setAbsolute() {
        this.absolute = true;
    }

    void multiplyValue(double multiplier) {
        this.ctMultiplier = multiplier;
        if (mode == Mode.STACKING_MULTIPLY) {
            this.value = ((this.value - 1F) * ((float) multiplier)) + 1F;
        } else {
            this.value *= multiplier;
        }
    }

    /**
     * Use this method for PerkConverters returning a new PerkAttributeModifier!
     */
    @Nonnull
    public PerkAttributeModifier convertModifier(String attributeType, Mode mode, float value) {
        if (absolute) {
            return this;
        }
        PerkAttributeType type = AttributeTypeRegistry.getType(attributeType);
        PerkAttributeModifier mod = this.createModifier(type, attributeType, mode, value);
        mod.setId(this.id);
        return mod;
    }

    /**
     * Use this method for creating extra Modifiers depending on a given modifier.
     */
    @Nonnull
    public PerkAttributeModifier gainAsExtraModifier(PerkConverter converter, String attributeType, Mode mode,
                                                     float value) {
        PerkAttributeModifier modifier = getCachedAttributeModifier(converter, attributeType, mode);
        if (modifier == null) {
            modifier = this.createModifier(AttributeTypeRegistry.getType(attributeType), attributeType, mode, value);
            modifier.setAbsolute();
            addModifierToCache(converter, attributeType, mode, modifier);
        }
        return modifier;
    }

    @Nullable
    protected PerkAttributeModifier getCachedAttributeModifier(PerkConverter converter, String attributeType,
                                                               Mode mode) {
        Table<String, Mode, PerkAttributeModifier> cachedModifiers = cachedConverters
            .computeIfAbsent(converter, (c) -> HashBasedTable.create());
        return cachedModifiers.get(attributeType, mode);
    }

    protected void addModifierToCache(PerkConverter converter, String attributeType, Mode mode,
                                      PerkAttributeModifier modifier) {
        Table<String, Mode, PerkAttributeModifier> cachedModifiers = cachedConverters
            .computeIfAbsent(converter, (c) -> HashBasedTable.create());
        cachedModifiers.put(attributeType, mode, modifier);
    }

    @Nonnull
    protected PerkAttributeModifier createModifier(@Nullable PerkAttributeType type, String attributeType, Mode mode,
                                                   float value) {
        if (type != null) {
            return type.createModifier(value, mode);
        } else {
            return new PerkAttributeModifier(attributeType, mode, value);
        }
    }

    // Should not be accessed directly unless for internal calculation purposes.
    // The actual effect of the modifier might depend on the player's AS-data.
    @Deprecated
    public final float getFlatValue() {
        return value;
    }

    public float getValue(EntityPlayer player, PlayerProgress progress) {
        return getFlatValue();
    }

    @SideOnly(Side.CLIENT)
    public float getValueForDisplay(EntityPlayer player, PlayerProgress progress) {
        return getValue(player, progress);
    }

    public Mode getMode() {
        return mode;
    }

    public String getAttributeType() {
        return attributeType;
    }

    @Nullable
    public PerkAttributeType resolveType() {
        return AttributeTypeRegistry.getType(attributeType);
    }

    protected String getUnlocalizedAttributeName() {
        PerkAttributeType type;
        if ((type = resolveType()) != null) {
            return type.getUnlocalizedName();
        }
        return "???";
    }

    @SideOnly(Side.CLIENT)
    public boolean hasDisplayString() {
        PerkAttributeType type;
        if ((type = resolveType()) != null) {
            return I18n.hasKey(type.getUnlocalizedName());
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedAttributeValue() {
        return getMode()
            .stringifyValue(getValueForDisplay(Minecraft.getMinecraft().player, ResearchManager.clientProgress));
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedModifierName() {
        return I18n.format(
            getMode().getUnlocalizedModifierName(
                getValueForDisplay(Minecraft.getMinecraft().player, ResearchManager.clientProgress)));
    }

    @SideOnly(Side.CLIENT)
    public String getAttributeDisplayFormat() {
        return I18n.format("perk.modifier.format");
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public String getLocalizedDisplayString() {
        if (!hasDisplayString()) {
            return null;
        }
        return String.format(
            getAttributeDisplayFormat(),
            getLocalizedAttributeValue(),
            getLocalizedModifierName(),
            I18n.format(getUnlocalizedAttributeName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeModifier that = (PerkAttributeModifier) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static enum Mode {

        ADDITION,
        ADDED_MULTIPLY,
        STACKING_MULTIPLY;

        public static Mode fromVanillaAttributeOperation(int op) {
            return values()[MathHelper.clamp(op, 0, values().length - 1)];
        }

        public int getVanillaAttributeOperation() {
            return ordinal();
        }

        // We don't need the explicit + addition to positive percentages
        public String stringifyValue(float number) {
            if (this == ADDITION) {
                String str = Integer.toString(Math.round(number));
                if (number > 0) {
                    str = "+" + str;
                }
                return str;
            } else {
                int nbr = Math.round(number * 100);
                return Integer.toString(Math.abs(this == STACKING_MULTIPLY ? 100 - nbr : nbr));
            }
        }

        public String getUnlocalizedModifierName(float number) {
            boolean positive;
            if (this == ADDITION) {
                positive = number > 0; // 0 would be kinda... weird as addition/subtraction modifier...
            } else {
                int nbr = Math.round(number * 100);
                positive = this == STACKING_MULTIPLY ? nbr > 100 : nbr > 0;
            }
            return getUnlocalizedModifierName(positive);
        }

        public String getUnlocalizedModifierName(boolean positive) {
            String base = positive ? "perk.modifier.%s.add" : "perk.modifier.%s.sub";
            return String.format(base, name().toLowerCase());
        }

    }

}
