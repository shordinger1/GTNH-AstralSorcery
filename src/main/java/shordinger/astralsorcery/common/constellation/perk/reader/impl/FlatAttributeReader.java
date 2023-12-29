/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.reader.impl;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeLimiter;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import shordinger.astralsorcery.common.constellation.perk.reader.AttributeReader;
import shordinger.astralsorcery.common.constellation.perk.reader.PerkStatistic;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.migration.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FlatAttributeReader
 * Created by HellFirePvP
 * Date: 19.01.2019 / 09:31
 */
public class FlatAttributeReader extends AttributeReader {

    protected final PerkAttributeType attribute;
    protected final float defaultValue;

    private boolean formatAsDecimal = false;

    public FlatAttributeReader(PerkAttributeType attribute, float defaultValue) {
        this.attribute = attribute;
        this.defaultValue = defaultValue;
    }

    public <T extends FlatAttributeReader> T formatAsDecimal() {
        this.formatAsDecimal = true;
        return (T) this;
    }

    @Override
    public double getDefaultValue(PlayerAttributeMap statMap, EntityPlayer player, Side side) {
        return this.defaultValue;
    }

    @Override
    public double getModifierValueForMode(PlayerAttributeMap statMap, EntityPlayer player, Side side,
                                          PerkAttributeModifier.Mode mode) {
        return statMap
            .getModifier(player, ResearchManager.getProgress(player, side), this.attribute.getTypeString(), mode);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public PerkStatistic getStatistics(PlayerAttributeMap statMap, EntityPlayer player) {
        Float limit = AttributeTypeLimiter.INSTANCE.getMaxLimit(this.attribute);
        String limitStr = limit == null ? "" : I18n.format("perk.reader.limit.default", MathHelper.floor(limit));

        double value = statMap.modifyValue(
            player,
            ResearchManager.getProgress(player, Side.CLIENT),
            this.attribute.getTypeString(),
            (float) this.getDefaultValue(statMap, player, Side.CLIENT));

        String postProcess = "";
        double post = AttributeEvent.postProcessModded(player, this.attribute, value);
        if (Math.abs(value - post) > 1E-4 && (limit == null || Math.abs(post - limit) > 1E-4)) {
            if (Math.abs(post) >= 1E-4) {
                postProcess = I18n.format("perk.reader.postprocess.default", formatForDisplay(post));
            }
            value = post;
        }

        return new PerkStatistic(this.attribute, formatForDisplay(value), limitStr, postProcess);
    }

    protected String formatForDisplay(double value) {
        String valueStr;
        if (this.formatAsDecimal) {
            valueStr = formatDecimal(value);
        } else {
            valueStr = String.valueOf(MathHelper.floor(value));
        }

        return (value >= 0 ? "+" : "") + valueStr;
    }

}
