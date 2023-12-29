/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.reader.AttributeReader;
import shordinger.astralsorcery.common.constellation.perk.reader.AttributeReaderRegistry;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeType
 * Created by HellFirePvP
 * Date: 08.07.2018 / 12:22
 */
public class PerkAttributeType {

    protected static final Random rand = new Random();

    // May be used by subclasses to more efficiently track who's got a perk applied
    private final Map<Side, List<UUID>> applicationCache = Maps.newHashMap();

    private final String type;
    private final boolean isOnlyMultiplicative;

    public PerkAttributeType(String type) {
        this(type, false);
    }

    public PerkAttributeType(String type, boolean isOnlyMultiplicative) {
        this.type = type;
        this.isOnlyMultiplicative = isOnlyMultiplicative;
    }

    public String getTypeString() {
        return type;
    }

    public boolean isMultiplicative() {
        return isOnlyMultiplicative;
    }

    public String getUnlocalizedName() {
        return String.format("perk.attribute.%s.name", getTypeString());
    }

    protected void init() {
    }

    @Nullable
    public AttributeReader getReader() {
        return AttributeReaderRegistry.getReader(this.getTypeString());
    }

    @Nonnull
    public PerkAttributeModifier createModifier(float modifier, PerkAttributeModifier.Mode mode) {
        if (isMultiplicative() && mode == PerkAttributeModifier.Mode.ADDITION) {
            throw new IllegalArgumentException("Tried creating addition-modifier for a multiplicative-only modifier!");
        }
        return new PerkAttributeModifier(getTypeString(), mode, modifier);
    }

    public void onApply(EntityPlayer player, Side side) {
        List<UUID> applied = applicationCache.computeIfAbsent(side, s -> Lists.newArrayList());
        if (!applied.contains(player.getUniqueID())) {
            applied.add(player.getUniqueID());
        }
    }

    public void onRemove(EntityPlayer player, Side side, boolean removedCompletely) {
        if (removedCompletely) {
            applicationCache.computeIfAbsent(side, s -> Lists.newArrayList())
                .remove(player.getUniqueID());
        }
    }

    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
    }

    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side,
                             boolean removedCompletely) {
    }

    public boolean hasTypeApplied(EntityPlayer player, Side side) {
        return applicationCache.computeIfAbsent(side, s -> Lists.newArrayList())
            .contains(player.getUniqueID());
    }

    public final void clear(Side side) {
        applicationCache.remove(side);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkAttributeType that = (PerkAttributeType) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
