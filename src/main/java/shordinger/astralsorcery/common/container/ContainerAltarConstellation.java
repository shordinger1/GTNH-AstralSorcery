/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.container;

import net.minecraft.entity.player.InventoryPlayer;

import shordinger.astralsorcery.common.tile.TileAltar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarConstellation
 * Created by HellFirePvP
 * Date: 02.11.2016 / 14:42
 */
public class ContainerAltarConstellation extends ContainerAltarAttunement {

    protected ContainerAltarConstellation(InventoryPlayer playerInv, TileAltar tileAltar, int altarGridSlotSize) {
        super(playerInv, tileAltar, altarGridSlotSize);
    }

    public ContainerAltarConstellation(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar, 21);
    }

    @Override
    void bindAltarInventory() {
        super.bindAltarInventory();

        addSlotToContainer(new SlotItemHandler(invHandler, 13, 102, 11));
        addSlotToContainer(new SlotItemHandler(invHandler, 14, 138, 11));

        addSlotToContainer(new SlotItemHandler(invHandler, 15, 84, 29));
        addSlotToContainer(new SlotItemHandler(invHandler, 16, 156, 29));

        addSlotToContainer(new SlotItemHandler(invHandler, 17, 84, 65));
        addSlotToContainer(new SlotItemHandler(invHandler, 18, 156, 65));

        addSlotToContainer(new SlotItemHandler(invHandler, 19, 102, 83));
        addSlotToContainer(new SlotItemHandler(invHandler, 20, 138, 83));
    }

}
