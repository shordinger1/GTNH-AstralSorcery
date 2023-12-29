/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerObservatory
 * Created by HellFirePvP
 * Date: 27.05.2018 / 07:36
 */
// Dummy container to allow remote opening and easier handling on serverside for dismounting the observatory.
public class ContainerObservatory extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

}
