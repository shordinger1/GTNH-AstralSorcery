/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.constellation.perk.types;

import shordinger.astralsorcery.common.constellation.perk.PerkConverter;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IConverterProvider
 * Created by HellFirePvP
 * Date: 03.08.2018 / 08:08
 */
public interface IConverterProvider {

    public Collection<PerkConverter> provideConverters(EntityPlayer player, Side side);

}
