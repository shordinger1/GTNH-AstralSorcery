/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.nodes;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreeGem;
import shordinger.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.item.gem.ItemPerkGem;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemSlotMajorPerk
 * Created by HellFirePvP
 * Date: 17.11.2018 / 20:17
 */
public class GemSlotMajorPerk extends MajorPerk implements GemSlotPerk {

    public GemSlotMajorPerk(String name, int x, int y) {
        super(name, x, y);
        disableTooltipCaching();
    }

    @Override
    protected PerkTreePoint<? extends GemSlotMajorPerk> initPerkTreePoint() {
        return new PerkTreeGem<>(this, getOffset());
    }

    @Override
    protected Collection<PerkAttributeModifier> getModifiers(EntityPlayer player, Side side) {
        Collection<PerkAttributeModifier> mods = super.getModifiers(player, side);
        if (!modifiersDisabled(player, side)) {
            ItemStack contained = getContainedItem(player, side);
            if (!contained.isEmpty() && contained.getItem() instanceof ItemPerkGem) {
                mods.addAll(ItemPerkGem.getModifiers(contained));
            }
        }
        return mods;
    }

    @Override
    public void onRemovePerkServer(EntityPlayer player, PlayerProgress progress, NBTTagCompound dataStorage) {
        super.onRemovePerkServer(player, progress, dataStorage);
        dropItemToPlayer(player, dataStorage);
    }

    @Override
    public String getUnlocalizedName() {
        if (this.ovrUnlocalizedNamePrefix != null) {
            return this.ovrUnlocalizedNamePrefix;
        }
        return "perk.astralsorcery.gemsocket";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        if (super.addLocalizedTooltip(tooltip)) {
            tooltip.add("");
        }
        if (canSeeClient()) {
            this.addTooltipInfo(tooltip);
        }
        return true;
    }
}
