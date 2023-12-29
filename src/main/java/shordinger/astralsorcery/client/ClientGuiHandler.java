/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.gui.*;
import shordinger.astralsorcery.client.gui.GuiConstellationPaper;
import shordinger.astralsorcery.client.gui.GuiHandTelescope;
import shordinger.astralsorcery.client.gui.GuiJournalProgression;
import shordinger.astralsorcery.client.gui.GuiKnowledgeFragment;
import shordinger.astralsorcery.client.gui.GuiMapDrawing;
import shordinger.astralsorcery.client.gui.GuiObservatory;
import shordinger.astralsorcery.client.gui.GuiSextantSelector;
import shordinger.astralsorcery.client.gui.GuiTelescope;
import shordinger.astralsorcery.client.gui.container.*;
import shordinger.astralsorcery.client.gui.container.GuiAltarAttunement;
import shordinger.astralsorcery.client.gui.container.GuiAltarConstellation;
import shordinger.astralsorcery.client.gui.container.GuiAltarDiscovery;
import shordinger.astralsorcery.client.gui.container.GuiAltarTrait;
import shordinger.astralsorcery.client.gui.container.GuiJournalContainer;
import shordinger.astralsorcery.common.CommonProxy;
import shordinger.astralsorcery.common.constellation.ConstellationRegistry;
import shordinger.astralsorcery.common.constellation.IConstellation;
import shordinger.astralsorcery.common.constellation.MoonPhase;
import shordinger.astralsorcery.common.item.ItemJournal;
import shordinger.astralsorcery.common.item.knowledge.ItemKnowledgeFragment;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.astralsorcery.common.tile.TileAltar;
import shordinger.astralsorcery.common.tile.TileMapDrawingTable;
import shordinger.astralsorcery.common.tile.TileObservatory;
import shordinger.astralsorcery.common.tile.TileTelescope;
import shordinger.astralsorcery.common.util.MiscUtils;
import shordinger.astralsorcery.common.util.data.Tuple;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientGuiHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 13:26
 */
public class ClientGuiHandler {

    @SideOnly(Side.CLIENT)
    public static Object openGui(CommonProxy.EnumGuiId guiType, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity t = null;
        if (guiType.getTileClass() != null) {
            t = MiscUtils.getTileAt(world, new BlockPos(x, y, z), guiType.getTileClass(), true);
            if (t == null) {
                return null;
            }
        }
        switch (guiType) {
            case TELESCOPE:
                return new GuiTelescope(player, (TileTelescope) t);
            case HAND_TELESCOPE:
                return new GuiHandTelescope();
            case CONSTELLATION_PAPER:
                IConstellation c = ConstellationRegistry.getConstellationById(x); // Suggested Constellation id;
                if (c == null) {
                    AstralSorcery.log.info("Tried opening ConstellationPaper GUI with out-of-range constellation id!");
                    return null;
                } else {
                    return new GuiConstellationPaper(c);
                }
            case ALTAR_DISCOVERY:
                return new GuiAltarDiscovery(player.inventory, (TileAltar) t);
            case ALTAR_ATTUNEMENT:
                return new GuiAltarAttunement(player.inventory, (TileAltar) t);
            case ALTAR_CONSTELLATION:
                return new GuiAltarConstellation(player.inventory, (TileAltar) t);
            case ALTAR_TRAIT:
                return new GuiAltarTrait(player.inventory, (TileAltar) t);
            case MAP_DRAWING:
                return new GuiMapDrawing((TileMapDrawingTable) t);
            case JOURNAL:
                return GuiJournalProgression.getOpenJournalInstance();
            case JOURNAL_STORAGE:
                ItemStack held = player.getHeldItem();
                if (!held.isEmpty()) {
                    if (held.getItem() instanceof ItemJournal) {
                        return new GuiJournalContainer(player.inventory, held, player.inventory.currentItem);
                    }
                }
            case OBSERVATORY:
                return new GuiObservatory(player, (TileObservatory) t);
            case SEXTANT:
                Tuple<EnumHand, ItemStack> heldSextant = MiscUtils.getMainOrOffHand(player, ItemsAS.sextant);
                if (heldSextant != null) {
                    return new GuiSextantSelector(heldSextant.value, heldSextant.key);
                }
            case KNOWLEDGE_CONSTELLATION:
                Tuple<EnumHand, ItemStack> handFragment = MiscUtils.getMainOrOffHand(player, ItemsAS.knowledgeFragment);
                Tuple<IConstellation, List<MoonPhase>> cstInfo = ItemKnowledgeFragment
                    .getConstellationInformation(handFragment.value);
                if (cstInfo != null) {
                    return new GuiKnowledgeFragment(cstInfo.key, cstInfo.value);
                }
            default:
                return null;
        }
    }

}
