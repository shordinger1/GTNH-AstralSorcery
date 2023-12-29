/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import net.minecraft.util.ResourceLocation;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.gui.GuiJournalPerkTree;
import shordinger.astralsorcery.client.gui.journal.GuiScreenJournal;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.IMinorConstellation;
import shordinger.astralsorcery.common.constellation.IWeakConstellation;
import shordinger.astralsorcery.common.data.fragment.KnowledgeFragment;
import shordinger.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchProgression;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryKnowledgeFragments
 * Created by HellFirePvP
 * Date: 23.09.2018 / 13:42
 */
public class RegistryKnowledgeFragments {

    public static void init() {
        KnowledgeFragmentManager mgr = KnowledgeFragmentManager.getInstance();

        ConstellationRegistry.getAllConstellations()
            .forEach(RegistryKnowledgeFragments::registerConstellationFragment);

        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.discovery.ancientshrine", ResearchProgression.findNode("SHRINES")));
        mgr.register(
            KnowledgeFragment.onResearchNodes("fragment.discovery.resowand", ResearchProgression.findNode("WAND")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.misc.altar",
                ResearchProgression.findNode("ALTAR1"),
                ResearchProgression.findNode("ALTAR2"),
                ResearchProgression.findNode("ALTAR3"),
                ResearchProgression.findNode("ALTAR4")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.discovery.startable",
                ResearchProgression.findNode("LINKTOOL"),
                ResearchProgression.findNode("LENS"),
                ResearchProgression.findNode("PRISM"),
                ResearchProgression.findNode("STARLIGHT_NETWORK"),
                ResearchProgression.findNode("COLL_CRYSTAL"),
                ResearchProgression.findNode("ENHANCED_COLLECTOR")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.lightwellprod", ResearchProgression.findNode("WELL")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.lightwelluses", ResearchProgression.findNode("WELL")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.crystalgrowth", ResearchProgression.findNode("CRYSTAL_GROWTH")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.crystaltools", ResearchProgression.findNode("TOOLS")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.grindstone", ResearchProgression.findNode("GRINDSTONE")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.misc.cannibalism",
                ResearchProgression.findNode("COLL_CRYSTAL"),
                ResearchProgression.findNode("ENHANCED_COLLECTOR"),
                ResearchProgression.findNode("SPEC_RELAY"),
                ResearchProgression.findNode("ALTAR1"),
                ResearchProgression.findNode("ALTAR2"),
                ResearchProgression.findNode("ALTAR3"),
                ResearchProgression.findNode("ALTAR4")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.caveillumplace", ResearchProgression.findNode("ILLUMINATOR")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.exploration.caveillumwand",
                ResearchProgression.findNode("ILLUMINATOR"),
                ResearchProgression.findNode("ILLUMINATION_WAND")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.exploration.nocturnal", ResearchProgression.findNode("NOC_POWDER")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.attunement.starlightchunks",
                ResearchProgression.findNode("LENS"),
                ResearchProgression.findNode("PRISM"),
                ResearchProgression.findNode("STARLIGHT_NETWORK"),
                ResearchProgression.findNode("COLL_CRYSTAL"),
                ResearchProgression.findNode("ENHANCED_COLLECTOR"),
                ResearchProgression.findNode("RIT_PEDESTAL")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.attunement.alignmentcharge",
                ResearchProgression.findNode("QUICK_CHARGE"),
                ResearchProgression.findNode("TOOL_WANDS"),
                ResearchProgression.findNode("GRAPPLE_WAND")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.attunement.attunement", ResearchProgression.findNode("ATT_PLAYER")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.attunement.ritualpedestal",
                ResearchProgression.findNode("RIT_PEDESTAL"),
                ResearchProgression.findNode("PED_ACCEL")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.constellation.colorlens", ResearchProgression.findNode("LENSES_EFFECTS")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.constellation.refractiontable",
                ResearchProgression.findNode("DRAWING_TABLE")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.constellation.treebeaconuse", ResearchProgression.findNode("TREEBEACON")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.constellation.treebeaconboost", ResearchProgression.findNode("TREEBEACON")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.constellation.inftool", ResearchProgression.findNode("CHARGED_TOOLS")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.constellation.illumwand",
                ResearchProgression.findNode("ILLUMINATION_WAND")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.constellation.prism", ResearchProgression.findNode("ENCHANTMENT_AMULET")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.constellation.clusterbonus",
                ResearchProgression.findNode("CEL_CRYSTAL_GROW")));
        mgr.register(
            KnowledgeFragment.onResearchNodes(
                "fragment.constellation.clusterspeed",
                ResearchProgression.findNode("CEL_CRYSTAL_GROW")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.constellation.ec3", ResearchProgression.findNode("ENHANCED_COLLECTOR")));
        mgr.register(
            KnowledgeFragment
                .onResearchNodes("fragment.radiance.fysallidic", ResearchProgression.findNode("BORE_HEAD_VORTEX")));
        mgr.register(
                new KnowledgeFragment(
                    new ResourceLocation(AstralSorcery.MODID, "fragment.misc.perks"),
                    "gui.journal.bm.perks.name") {

                    @Override
                    public boolean isVisible(GuiScreenJournal journalGui) {
                        return journalGui instanceof GuiJournalPerkTree;
                    }
                })
            .setCanSeeTest(prog -> prog.getAttunedConstellation() != null);
        mgr.register(
            KnowledgeFragment.onResearchNodes("fragment.perk_gems", ResearchProgression.findNode("ATT_PERK_GEMS")));
    }

    private static void registerConstellationFragment(IConstellation cst) {
        KnowledgeFragmentManager mgr = KnowledgeFragmentManager.getInstance();
        String cstKey = "fragment.constellation." + cst.getSimpleName();

        mgr.register(KnowledgeFragment.onConstellations(cstKey + ".showup", cst));
        mgr.register(
            KnowledgeFragment.onConstellations(cstKey + ".potions", cst)
                .setCanSeeTest(KnowledgeFragment.hasTier(ProgressionTier.CONSTELLATION_CRAFT)));
        mgr.register(
            KnowledgeFragment.onConstellations(cstKey + ".enchantments", cst)
                .setCanSeeTest(KnowledgeFragment.hasTier(ProgressionTier.CONSTELLATION_CRAFT)));

        if (cst instanceof IWeakConstellation) {
            mgr.register(
                KnowledgeFragment.onConstellations(cstKey + ".ritual", cst)
                    .setCanSeeTest(KnowledgeFragment.hasTier(ProgressionTier.ATTUNEMENT)));
            mgr.register(
                KnowledgeFragment.onConstellations(cstKey + ".ritual.corrupted", cst)
                    .setCanSeeTest(KnowledgeFragment.hasTier(ProgressionTier.TRAIT_CRAFT)));
            mgr.register(
                KnowledgeFragment.onConstellations(cstKey + ".mantle", cst)
                    .setCanSeeTest(KnowledgeFragment.hasTier(ProgressionTier.CONSTELLATION_CRAFT)));
        } else if (cst instanceof IMinorConstellation) {
            mgr.register(
                KnowledgeFragment.onConstellations(cstKey + ".trait", cst)
                    .setCanSeeTest(KnowledgeFragment.hasTier(ProgressionTier.CONSTELLATION_CRAFT)));
        }
    }

}
