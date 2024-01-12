/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierPerk
 * Created by HellFirePvP
 * Date: 09.07.2018 / 15:10
 */
// Usable for most/many cases. Handles also all basic stuff around modifiers and converters
public class AttributeModifierPerk extends AttributeConverterPerk {

    private List<PerkAttributeModifier> typeModifierList = Lists.newArrayList();

    public AttributeModifierPerk(String name, int x, int y) {
        super(name, x, y);
    }

    public AttributeModifierPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Nullable
    public <T extends PerkAttributeModifier> T addModifier(float modifier, PerkAttributeModifier.Mode mode,
                                                           String type) {
        PerkAttributeType attrType = AttributeTypeRegistry.getType(type);
        if (attrType != null) {
            return addModifier((T) attrType.createModifier(modifier, mode));
        }
        return null;
    }

    @Nullable
    protected <T extends PerkAttributeModifier> T addModifier(T modifier) {
        typeModifierList.add(modifier);
        return modifier;
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        typeModifierList.forEach(t -> t.multiplyValue(multiplier));
    }

    protected Collection<PerkAttributeModifier> getModifiers(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(this.typeModifierList);
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {
        super.applyPerkLogic(player, side);

        LogCategory.PERKS.info(() -> "Applying modifiers of " + this.getRegistryName() + " on " + side.name());

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(player, prog, modifier, this));
            for (PerkAttributeModifier mod : modify) {

                PerkAttributeModifier preMod = mod;
                LogCategory.PERKS.info(() -> "Applying unique modifier " + preMod.getId());

                mod = attr.convertModifier(player, prog, mod, this);

                PerkAttributeModifier postMod = mod;
                LogCategory.PERKS.info(() -> "Applying converted modifier " + postMod.getId());
                if (!attr.applyModifier(player, mod.getAttributeType(), mod)) {
                    LogCategory.PERKS.warn(() -> "Could not apply modifier " + postMod.getId() + " - already applied!");
                }
            }
        }
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        LogCategory.PERKS.info(() -> "Removing modifiers of " + this.getRegistryName() + " on " + side.name());

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(player, prog, modifier, this));
            for (PerkAttributeModifier mod : modify) {

                PerkAttributeModifier preMod = mod;
                LogCategory.PERKS.info(() -> "Removing unique modifier " + preMod.getId());

                mod = attr.convertModifier(player, prog, mod, this);

                PerkAttributeModifier postMod = mod;
                LogCategory.PERKS.info(() -> "Removing converted modifier " + postMod.getId());
                if (!attr.removeModifier(player, mod.getAttributeType(), mod)) {
                    LogCategory.PERKS.warn(() -> "Could not remove modifier " + postMod.getId() + " - not applied!");
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        Collection<PerkAttributeModifier> modifiers = this.getModifiers(Minecraft.getMinecraft().thePlayer, Side.CLIENT);
        boolean addEmptyLine = !modifiers.isEmpty();

        if (canSeeClient()) {
            for (PerkAttributeModifier modifier : modifiers) {
                String modifierDisplay = modifier.getLocalizedDisplayString();
                if (modifierDisplay != null) {
                    tooltip.add(modifierDisplay);
                } else {
                    addEmptyLine = false;
                }
            }
        }

        return addEmptyLine;
    }
}
