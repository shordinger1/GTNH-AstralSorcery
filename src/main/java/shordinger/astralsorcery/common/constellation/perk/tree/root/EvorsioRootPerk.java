/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.tree.root;

import shordinger.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import shordinger.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import shordinger.astralsorcery.common.data.research.PlayerProgress;
import shordinger.astralsorcery.common.data.research.ResearchManager;
import shordinger.astralsorcery.common.event.AttributeEvent;
import shordinger.astralsorcery.common.lib.Constellations;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.log.LogCategory;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.event.world.BlockEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.EventPriority;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EvorsioRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:41
 */
public class EvorsioRootPerk extends RootPerk {

    public EvorsioRootPerk(int x, int y) {
        super("evorsio", Constellations.evorsio, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        Side side = event.getPlayer().world.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        EntityPlayer player = event.getPlayer();
        if (player != null && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (!prog.hasPerkEffect(this)) {
                return;
            }

            IBlockState broken = event.getState();
            World world = event.getWorld();
            float gainedExp;
            try {
                gainedExp = broken.getBlockHardness(world, event.getPos());
            } catch (Exception exc) {
                gainedExp = 0.5F;
            }
            if (gainedExp <= 0) {
                return; //Unbreakable lol. you're not getting exp for that.
            }
            gainedExp *= 0.15F;
            gainedExp *= expMultiplier;
            gainedExp = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, gainedExp);
            gainedExp = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(player, prog, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, gainedExp);
            gainedExp = (float) Math.sqrt(gainedExp);
            gainedExp = AttributeEvent.postProcessModded(player, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, gainedExp);

            float xpGain = gainedExp;
            LogCategory.PERKS.info(() -> "Grant " + xpGain + " exp to " + player.getName() + " (Evorsio)");

            ResearchManager.modifyExp(player, xpGain);
        }
    }

}
