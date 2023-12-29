/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.structure;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MatchableStructure
 * Created by HellFirePvP
 * Date: 02.12.2018 / 10:17
 */
public interface MatchableStructure {

    @Nonnull
    public ResourceLocation getRegistryName();

}
