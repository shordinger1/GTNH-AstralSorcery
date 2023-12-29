/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.data.research;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import shordinger.astralsorcery.Tags;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchProgression
 * Created by HellFirePvP
 * Date: 10.08.2016 / 13:38
 */
public enum ResearchProgression {

    DISCOVERY(0, ProgressionTier.DISCOVERY),
    BASIC_CRAFT(1, ProgressionTier.BASIC_CRAFT, DISCOVERY),
    ATTUNEMENT(2, ProgressionTier.ATTUNEMENT, BASIC_CRAFT),
    CONSTELLATION(3, ProgressionTier.CONSTELLATION_CRAFT, ATTUNEMENT),
    RADIANCE(4, ProgressionTier.TRAIT_CRAFT, CONSTELLATION),
    BRILLIANCE(5, ProgressionTier.BRILLIANCE, RADIANCE);

    private final int progressId;
    private final List<ResearchProgression> preConditions = new LinkedList<>();
    private final List<ResearchNode> researchNodes = new LinkedList<>();
    private final ProgressionTier requiredProgress;
    private final String unlocName;

    private static final Map<Integer, ResearchProgression> BY_ID = new HashMap<>();
    private static final Map<String, ResearchProgression> BY_NAME = new HashMap<>();

    private ResearchProgression(int id, ProgressionTier requiredProgress, ResearchProgression... preConditions) {
        this(id, requiredProgress, Arrays.asList(preConditions));
    }

    private ResearchProgression(int id, ProgressionTier requiredProgress, List<ResearchProgression> preConditions) {
        this.preConditions.addAll(preConditions);
        this.requiredProgress = requiredProgress;
        this.progressId = id;
        this.unlocName = Tags.MODID + ".journal.cluster." + name().toLowerCase() + ".name";
    }

    void addResearchToGroup(ResearchNode res) {
        for (ResearchNode node : researchNodes) {
            if (node.renderPosX == res.renderPosX && node.renderPosZ == res.renderPosZ) {
                throw new IllegalArgumentException(
                    "Tried to register 2 Research Nodes at the same position at x=" + res.renderPosX
                        + ", z="
                        + res.renderPosZ
                        + "! "
                        + "Present: "
                        + node.getUnLocalizedName()
                        + " - Tried to set: "
                        + res.getUnLocalizedName());
            }
        }
        this.researchNodes.add(res);
    }

    public List<ResearchNode> getResearchNodes() {
        return researchNodes;
    }

    public Registry getRegistry() {
        return new Registry(this);
    }

    /*
     * public boolean tryStepTo(EntityPlayer player, boolean force) {
     * return (force || canStepTo(player)) && ResearchManager.forceUnsafeResearchStep(player, this);
     * }
     * public boolean canStepTo(EntityPlayer player) {
     * PlayerProgress progress = ResearchManager.getProgress(player);
     * if(progress == null) return false;
     * List<ResearchProgression> playerResearchProgression = progress.getResearchProgression();
     * ProgressionTier playerTier = progress.getTierReached();
     * return playerTier.isThisLaterOrEqual(requiredProgress) && playerResearchProgression.containsAll(preConditions);
     * }
     */

    public ProgressionTier getRequiredProgress() {
        return requiredProgress;
    }

    public List<ResearchProgression> getPreConditions() {
        return Collections.unmodifiableList(preConditions);
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public int getProgressId() {
        return progressId;
    }

    public static ResearchProgression getById(int id) {
        return BY_ID.get(id);
    }

    public static ResearchProgression getByEnumName(String name) {
        return BY_NAME.get(name);
    }

    @Nullable
    public static ResearchNode findNode(String name) {
        for (ResearchProgression prog : values()) {
            for (ResearchNode node : prog.getResearchNodes()) {
                if (node.getSimpleName()
                    .equals(name)) {
                    return node;
                }
            }
        }
        return null;
    }

    @Nonnull
    public static Collection<ResearchProgression> findProgression(ResearchNode n) {
        Collection<ResearchProgression> progressions = Lists.newArrayList();
        for (ResearchProgression prog : values()) {
            if (prog.getResearchNodes()
                .contains(n)) {
                progressions.add(prog);
            }
        }
        return progressions;
    }

    static {
        for (ResearchProgression progress : values()) {
            BY_ID.put(progress.progressId, progress);
            BY_NAME.put(progress.name(), progress);
        }
    }

    public static class Registry {

        private final ResearchProgression prog;

        public Registry(ResearchProgression prog) {
            this.prog = prog;
        }

        public void register(ResearchNode node) {
            prog.addResearchToGroup(node);
        }

    }

}
