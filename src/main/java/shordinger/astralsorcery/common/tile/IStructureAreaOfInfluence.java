/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.tile;

import java.awt.*;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.util.ILocatable;
import shordinger.astralsorcery.migration.block.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStructureAreaOfInfluence
 * Created by HellFirePvP
 * Date: 26.08.2018 / 09:39
 */
public interface IStructureAreaOfInfluence extends ILocatable {

    @Nullable
    @SideOnly(Side.CLIENT)
    public Color getEffectRenderColor();

    @SideOnly(Side.CLIENT)
    default public BlockPos getActualRenderOffsetPos() {
        return this.getLocationPos();
    }

    public int getDimensionId();

    public double getRadius();

    public boolean providesEffect();

}
