/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkConverter;
import shordinger.astralsorcery.common.constellation.perk.ProgressGatedPerk;
import shordinger.astralsorcery.common.constellation.perk.types.IConverterProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeConverterPerk
 * Created by HellFirePvP
 * Date: 03.08.2018 / 07:35
 */
public abstract class AttributeConverterPerk extends ProgressGatedPerk implements IConverterProvider {

    private List<PerkConverter> converters = Lists.newArrayList();

    public AttributeConverterPerk(String name, int x, int y) {
        super(name, x, y);
    }

    public AttributeConverterPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    public <T> T addConverter(PerkConverter converter) {
        this.converters.add(converter);
        return (T) this;
    }

    public <T> T addRangedConverter(double radius, PerkConverter converter) {
        this.converters.add(
            converter.asRangedConverter(
                new Point.Double(
                    this.getOffset()
                        .getX(),
                    this.getOffset()
                        .getY()),
                radius));
        return (T) this;
    }

    @Override
    public List<PerkConverter> provideConverters(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(converters);
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
    }
}
