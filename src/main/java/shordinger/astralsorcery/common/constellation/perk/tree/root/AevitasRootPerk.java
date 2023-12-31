/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.root;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.BlockEvent;
import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.astralsorcery.migration.block.BlockPos;
import shordinger.astralsorcery.migration.block.IBlockState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AevitasRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:45
 */
public class AevitasRootPerk extends RootPerk {

    private static final int trackLength = 20;
    private final Map<UUID, Queue<BlockPos>> plInteractMap = new HashMap<>();
    private final Map<UUID, Deque<IBlockState>> plDimReturns = new HashMap<>();

    public AevitasRootPerk(int x, int y) {
        super("aevitas", Constellations.aevitas, x, y);
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        if (side == Side.SERVER) {
            plInteractMap.remove(player.getUniqueID());
            plDimReturns.remove(player.getUniqueID());
        }
    }

    @Override
    public void clearCaches(Side side) {
        super.clearCaches(side);

        if (side == Side.SERVER) {
            plInteractMap.clear();
            plDimReturns.clear();
        }
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        EntityPlayer player = event.player;
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        Deque<IBlockState> dim = plDimReturns.computeIfAbsent(player.getUniqueID(), u -> new LinkedList<>());
        while (dim.size() >= trackLength) {
            dim.pollLast();
        }
        float used = 0;
        for (IBlockState placed : dim) {
            if (MiscUtils.matchStateExact((IBlockState) event.block, placed)) {
                used++;
            }
        }
        float same;
        if (dim.size() <= 0) {
            same = 1F;
        } else {
            same = 0.4F + (1F - (used / trackLength)) * 0.6F;
        }
        dim.addFirst((IBlockState) event.block);

        BlockPos pos = event.getPos();
        Queue<BlockPos> tracked = plInteractMap
            .computeIfAbsent(player.getUniqueID(), u -> new ArrayDeque<>(trackLength));
        if (!tracked.contains(pos)) {
            tracked.add(pos);

            float xp = Math.max(
                event.getPlacedBlock()
                    .getBlockHardness(event.world, event.getPos()) / 20F,
                1);
            xp *= expMultiplier;
            xp *= same;
            xp = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, xp);
            xp = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, xp);
            xp = AttributeEvent.postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, xp);

            float expGain = xp;
            LogCategory.PERKS.info(() -> "Grant " + expGain + " exp to " + player.getDisplayName() + " (Aevitas)");

            ResearchManager.modifyExp(player, expGain);
        }

    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        Side side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        if (!prog.hasPerkEffect(this)) {
            return;
        }

        Queue<BlockPos> tracked = plInteractMap
            .computeIfAbsent(player.getUniqueID(), u -> new ArrayDeque<>(trackLength));
        if (tracked.contains(event.getPos())) {
            return;
        }
        while (tracked.size() >= trackLength) {
            tracked.poll();
        }
        tracked.add(event.getPos());
    }

}
