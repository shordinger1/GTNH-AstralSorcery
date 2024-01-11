/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.root;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.PlayerActivityManager;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.stats.StatBase;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.stats.StatisticsManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VicioRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 14:27
 */
public class VicioRootPerk extends RootPerk implements IPlayerTickPerk {

    private Map<StatBase, Map<UUID, Integer>> moveTrackMap = new HashMap<>();

    public VicioRootPerk(int x, int y) {
        super("vicio", Constellations.vicio, x, y);
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        if (side == Side.SERVER) {
            this.moveTrackMap.computeIfAbsent(StatList.WALK_ONE_CM, s -> new HashMap<>())
                .remove(player.getUniqueID());
            this.moveTrackMap.computeIfAbsent(StatList.SPRINT_ONE_CM, s -> new HashMap<>())
                .remove(player.getUniqueID());
            this.moveTrackMap.computeIfAbsent(StatList.FLY_ONE_CM, s -> new HashMap<>())
                .remove(player.getUniqueID());
            this.moveTrackMap.computeIfAbsent(StatList.AVIATE_ONE_CM, s -> new HashMap<>())
                .remove(player.getUniqueID());
        }
    }

    @Override
    public void clearCaches(Side side) {
        super.clearCaches(side);

        if (side == Side.SERVER) {
            this.moveTrackMap.clear();
        }
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER && player instanceof EntityPlayerMP) {
            UUID uuid = player.getUniqueID();
            StatisticsManager manager = ((EntityPlayerMP) player).getStatFile();
            int walked = manager.readStat(StatList.WALK_ONE_CM);
            int sprint = manager.readStat(StatList.SPRINT_ONE_CM);
            int flown = manager.readStat(StatList.FLY_ONE_CM);
            int elytra = manager.readStat(StatList.AVIATE_ONE_CM);

            int lastWalked = this.moveTrackMap.computeIfAbsent(StatList.WALK_ONE_CM, s -> new HashMap<>())
                .computeIfAbsent(uuid, u -> walked);
            int lastSprint = this.moveTrackMap.computeIfAbsent(StatList.SPRINT_ONE_CM, s -> new HashMap<>())
                .computeIfAbsent(uuid, u -> sprint);
            int lastFly = this.moveTrackMap.computeIfAbsent(StatList.FLY_ONE_CM, s -> new HashMap<>())
                .computeIfAbsent(uuid, u -> flown);
            int lastElytra = this.moveTrackMap.computeIfAbsent(StatList.AVIATE_ONE_CM, s -> new HashMap<>())
                .computeIfAbsent(uuid, u -> elytra);

            float added = 0;

            if (walked > lastWalked) {
                added += Math.min(walked - lastWalked, 500F);
                if (added >= 500F) {
                    added = 500F;
                }
                this.moveTrackMap.get(StatList.WALK_ONE_CM)
                    .put(uuid, walked);
            }
            if (sprint > lastSprint) {
                added += Math.min(sprint - lastSprint, 500F);
                if (added >= 500F) {
                    added = 500F;
                }
                added *= 1.2F;
                this.moveTrackMap.get(StatList.SPRINT_ONE_CM)
                    .put(uuid, sprint);
            }
            if (flown > lastFly) {
                added += Math.min(flown - lastFly, 500F);
                added *= 0.4F;
                this.moveTrackMap.get(StatList.FLY_ONE_CM)
                    .put(uuid, flown);
            }
            if (elytra > lastElytra) {
                added += Math.min(elytra - lastElytra, 500F);
                added *= 0.8F;
                this.moveTrackMap.get(StatList.AVIATE_ONE_CM)
                    .put(uuid, flown);
            }

            if (!PlayerActivityManager.INSTANCE.isPlayerActiveServer(player)) {
                return;
            }

            if (added > 0) {
                PlayerProgress prog = ResearchManager.getProgress(player, side);

                added *= 0.025F;
                added *= expMultiplier;
                added = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, added);
                added = PerkAttributeHelper.getOrCreateMap(player, side)
                    .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, added);
                added = AttributeEvent.postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, added);

                float xpGain = added;
                LogCategory.PERKS.info(() -> "Grant " + xpGain + " exp to " + player.getName() + " (Vicio)");

                ResearchManager.modifyExp(player, xpGain);
            }
        }
    }

}
