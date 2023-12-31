/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.item.base;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IOBJItem
 * Created by HellFirePvP
 * Date: 22.01.2017 / 15:01
 */
public interface IOBJItem {

    // If false is returned, getOBJModelNames will be queried to applyServer OBJ resource locations directly instead of
    // remotely.
    @SideOnly(Side.CLIENT)
    default public boolean hasOBJAsSubmodelDefinition() {
        return false;
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    default public String[] getOBJModelNames() {
        return null;
    }

}
