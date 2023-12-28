/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.client.gui.journal.GuiJournalPages;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.data.research.ResearchNode;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.util.ItemComparator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryBookLookups
 * Created by HellFirePvP
 * Date: 27.12.2016 / 18:39
 */
public class RegistryBookLookups {

    private static Map<ItemStack, LookupInfo> lookupMap = new HashMap<>();

    @Nullable
    public static LookupInfo tryGetPage(EntityPlayer querying, Side side, ItemStack search) {
        for (ItemStack compare : lookupMap.keySet()) {
            if (ItemComparator
                .compare(search, compare, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_WILDCARD)) {
                LookupInfo info = lookupMap.get(compare);
                PlayerProgress prog = ResearchManager.getProgress(querying, side);
                if (prog.getResearchProgression()
                    .contains(info.neededKnowledge) && info.node.canSee(prog)) {
                    return info;
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static void openLookupJournalPage(LookupInfo info) {
        if (info == null) return;

        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        Minecraft.getMinecraft()
            .displayGuiScreen(new GuiJournalPages(current, info.node, info.pageIndex));
    }

    public static void registerItemLookup(ItemStack stack, ResearchNode parentNode, int nodePage,
                                          ResearchProgression neededProgression) {
        lookupMap.put(stack, new LookupInfo(parentNode, nodePage, neededProgression));
    }

    public static class LookupInfo {

        public final ResearchNode node;
        public final int pageIndex;
        public final ResearchProgression neededKnowledge;

        public LookupInfo(ResearchNode node, int pageIndex, ResearchProgression neededKnowledge) {
            this.node = node;
            this.pageIndex = pageIndex;
            this.neededKnowledge = neededKnowledge;
        }
    }

}
