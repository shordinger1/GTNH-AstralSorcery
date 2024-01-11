/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.reader.impl;

import shordinger.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeLimiter;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.constellation.perk.reader.AttributeReader;
import shordinger.astralsorcery.common.constellation.perk.reader.PerkStatistic;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PercentageAttributeReader
 * Created by HellFirePvP
 * Date: 19.01.2019 / 10:12
 */
public class PercentageAttributeReader extends AttributeReader {

    protected final PerkAttributeType attribute;
    protected float defaultValue;

    public PercentageAttributeReader(PerkAttributeType attribute) {
        this.attribute = attribute;
        this.defaultValue = attribute.isMultiplicative() ? 1F : 0F;
    }

    public <T extends PercentageAttributeReader> T setDefaultValue(float defaultValue) {
        if (!attribute.isMultiplicative()) { //Percentage modifiers with a non-zero base make no sense
            this.defaultValue = defaultValue;
        }
        return (T) this;
    }

    @Override
    public double getDefaultValue(PlayerAttributeMap statMap, EntityPlayer player, Side side) {
        return this.defaultValue;
    }

    @Override
    public double getModifierValueForMode(PlayerAttributeMap statMap, EntityPlayer player, Side side,
                                          PerkAttributeModifier.Mode mode) {
        return statMap.getModifier(player, ResearchManager.getProgress(player, side),
                this.attribute.getTypeString(), mode);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public PerkStatistic getStatistics(PlayerAttributeMap statMap, EntityPlayer player) {
        Float limit = AttributeTypeLimiter.INSTANCE.getMaxLimit(this.attribute);
        String limitStr = limit == null ? "" :
                I18n.format("perk.reader.limit.percent", MathHelper.floor(limit * 100F));

        double value = statMap.modifyValue(player, ResearchManager.getProgress(player, Side.CLIENT),
                this.attribute.getTypeString(), (float) getDefaultValue(statMap, player, Side.CLIENT));

        String postProcess = "";
        double postValue = AttributeEvent.postProcessModded(player, this.attribute, value);
        if (Math.abs(value - postValue) > 1E-4 &&
                (limit == null || Math.abs(postValue - limit) > 1E-4)) {
            if (Math.abs(postValue) >= 1E-4) {
                postProcess = I18n.format("perk.reader.postprocess.default", formatForDisplay(postValue));
            }
            value = postValue;
        }

        if (attribute.isMultiplicative()) {
            value -= 1F;
        }

        return new PerkStatistic(this.attribute, formatForDisplay(value), limitStr, postProcess);
    }

    protected String formatForDisplay(double value) {
        value *= 100F;
        return (value >= 0 ? "+" : "") + formatDecimal(value) + "%";
    }
}
