/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.lib;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Materials
 * Created by HellFirePvP
 * Date: 14.09.2016 / 12:11
 */
public class Materials {

    public static final NoPushMaterial MATERIAL_NO_PUSH = new NoPushMaterial();

    public static class NoPushMaterial extends Material {

        private int mobilityFlag;

        public NoPushMaterial() {
            super(MapColor.AIR);
            setNoPushMobility();
        }

        public boolean blocksMovement() {
            return true;
        }

        protected Material setNoPushMobility() {
            this.mobilityFlag = 1;
            return this;
        }

        public int getMaterialMobility() {
            return this.mobilityFlag;
        }

    }

}
