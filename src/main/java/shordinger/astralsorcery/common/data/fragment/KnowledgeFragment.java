/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.fragment;

import java.util.*;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.Tags;
import shordinger.astralsorcery.client.gui.GuiJournalConstellationDetails;
import shordinger.astralsorcery.client.gui.journal.GuiJournalPages;
import shordinger.astralsorcery.client.gui.journal.GuiScreenJournal;
import shordinger.astralsorcery.client.util.ClientConstellationGenerator;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.MoonPhase;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ProgressionTier;
import shordinger.astralsorcery.common.data.research.ResearchNode;
import shordinger.astralsorcery.common.data.research.ResearchProgression;
import shordinger.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KnowledgeFragment
 * Created by HellFirePvP
 * Date: 22.09.2018 / 17:57
 */
public abstract class KnowledgeFragment {

    private static final Predicate<PlayerProgress> TRUE = (p) -> true;

    private final ResourceLocation name;
    private final String unlocPrefix;
    private Predicate<PlayerProgress> canSeeTest = TRUE;
    private Predicate<PlayerProgress> canDiscoverTest = TRUE;

    public KnowledgeFragment(ResourceLocation name, String unlocalizedPrefix) {
        this.name = name;
        this.unlocPrefix = unlocalizedPrefix;
    }

    public static KnowledgeFragment onConstellations(String name, IConstellation... constellations) {
        return onConstellations(new ResourceLocation(Tags.MODID, name), constellations);
    }

    public static KnowledgeFragment onConstellations(ResourceLocation name, IConstellation... constellations) {
        List<IConstellation> cst = Arrays.asList(constellations);
        IConstellation c = Iterables.getFirst(cst, null);
        return new KnowledgeFragment(name, c == null ? "" : c.getUnlocalizedName()) {

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible(GuiScreenJournal journalGui) {
                return journalGui instanceof GuiJournalConstellationDetails && MiscUtils
                    .contains(cst, n -> n.equals(((GuiJournalConstellationDetails) journalGui).getConstellation()));
            }
        }
            // Any involved constellation discovered
            .setCanSeeTest(prog -> {
                for (IConstellation con : cst) {
                    if (prog.hasConstellationDiscovered(con)) {
                        return true;
                    }
                }
                return false;
            });
    }

    public static KnowledgeFragment onResearchNodes(String name, ResearchNode... nodes) {
        return onResearchNodes(new ResourceLocation(Tags.MODID, name), nodes);
    }

    private static KnowledgeFragment onResearchNodes(ResourceLocation name, ResearchNode... nodes) {
        List<ResearchNode> nds = Arrays.asList(nodes);
        ResearchNode nd = Iterables.getFirst(nds, null);
        return new KnowledgeFragment(name, nd == null ? "" : nd.getUnLocalizedName()) {

            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible(GuiScreenJournal journalGui) {
                return journalGui instanceof GuiJournalPages
                    && MiscUtils.contains(nds, n -> n.equals(((GuiJournalPages) journalGui).getResearchNode()));
            }
        }
            // Any preconditions visible
            .setCanSeeTest(prog -> {
                for (ResearchNode n : nds) {
                    if (!n.canSee(prog)) {
                        continue;
                    }
                    for (ResearchProgression rProg : ResearchProgression.findProgression(n)) {
                        if (prog.getResearchProgression()
                            .contains(rProg)) {
                            return true;
                        }
                    }
                }
                return false;
            })
            // All preconditions visible
            .setCanDiscoverTest(prog -> {
                for (ResearchNode n : nds) {
                    if (!n.canSee(prog)) {
                        return false;
                    }
                    for (ResearchProgression rProg : ResearchProgression.findProgression(n)) {
                        if (!prog.getResearchProgression()
                            .contains(rProg)) {
                            return false;
                        }
                    }
                }
                return true;
            });
    }

    public KnowledgeFragment setCanSeeTest(Predicate<PlayerProgress> canSeeTest) {
        this.canSeeTest = canSeeTest;
        return this;
    }

    public KnowledgeFragment setCanDiscoverTest(Predicate<PlayerProgress> canDiscoverTest) {
        this.canDiscoverTest = canDiscoverTest;
        return this;
    }

    public static Predicate<PlayerProgress> hasTier(ProgressionTier tier) {
        return (p) -> p.getTierReached()
            .isThisLaterOrEqual(tier);
    }

    public static Predicate<PlayerProgress> discoveredConstellation(IConstellation cst) {
        return (p) -> p.hasConstellationDiscovered(cst);
    }

    // If this knowledge fragment is gated behind the random constellation minigame
    @SideOnly(Side.CLIENT)
    private boolean isConstellationGated(long seed) {
        return new Random(seed).nextInt(4) == 0;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public IConstellation getDiscoverConstellation(long seed) {
        if (!isConstellationGated(seed)) {
            return null;
        }
        ClientConstellationGenerator.ClientConstellation cst = ClientConstellationGenerator.generateRandom(seed);
        cst.setFragment(this);
        return cst;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    public List<MoonPhase> getShowupPhases(long seed) {
        if (!isConstellationGated(seed)) {
            return Lists.newArrayList();
        }
        Random r = new Random(seed);
        int amt = 2 + r.nextInt(2);
        List<MoonPhase> phases = new ArrayList<>(amt);
        for (int i = 0; i < amt; i++) {
            MoonPhase phase;
            do {
                phase = MoonPhase.values()[r.nextInt(MoonPhase.values().length)];
            } while (phases.contains(phase));
            phases.add(phase);
        }
        return phases;
    }

    // If the content of the knowledge fragment can be seen at the current progress
    // Might be earlier than #canDiscover
    public boolean canSee(PlayerProgress progress) {
        return canSeeTest.test(progress);
    }

    // If the knowledge fragment can be discovered at this stage
    // Might be later than #canSee
    // Always includes #canSee
    public boolean canDiscover(PlayerProgress progress) {
        return canSeeTest.test(progress) && canDiscoverTest.test(progress);
    }

    @SideOnly(Side.CLIENT)
    public abstract boolean isVisible(GuiScreenJournal journalGui);

    public String getUnlocalizedName() {
        return String.format("%s.name", getLocalizationBaseString());
    }

    public String getUnlocalizedBookmark() {
        return String.format("%s.bookmark", getLocalizationBaseString());
    }

    public String getUnlocalizedPage() {
        return String.format("%s.description", getLocalizationBaseString());
    }

    private String getLocalizationBaseString() {
        return String.format("knowledge.%s.%s", name.getResourceDomain(), name.getResourcePath());
    }

    @SideOnly(Side.CLIENT)
    public boolean isFullyPresent() {
        return I18n.hasKey(getUnlocalizedName()) && I18n.hasKey(getUnlocalizedBookmark())
            && I18n.hasKey(getUnlocalizedPage())
            && (this.unlocPrefix.isEmpty() || I18n.hasKey(this.unlocPrefix));
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format(getUnlocalizedName());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedBookmark() {
        return I18n.format(getUnlocalizedBookmark());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedPage() {
        return I18n.format(getUnlocalizedPage());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedIndexName() {
        return this.unlocPrefix.isEmpty() ? this.getLocalizedName()
            : String.format("%s: %s", I18n.format(this.unlocPrefix), this.getLocalizedName());
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeFragment that = (KnowledgeFragment) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name.toString());
    }
}
