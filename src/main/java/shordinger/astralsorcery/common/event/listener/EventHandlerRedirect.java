/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.event.listener;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import shordinger.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.core.ASMCallHook;
import shordinger.wrapper.net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerRedirect
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:51
 */
public class EventHandlerRedirect {

    @ASMCallHook
    public static float getSunBrightnessFactorInj(float prevBrightness, World world) {
        if (Config.weakSkyRendersWhitelist.contains(world.provider.getDimension())) {
            return prevBrightness;
        }
        WorldSkyHandler wsh = ConstellationSkyHandler.getInstance()
            .getWorldHandler(world);
        if (wsh != null && wsh.dayOfSolarEclipse && wsh.solarEclipse) {
            int eclTick = wsh.solarEclipseTick;
            if (eclTick >= ConstellationSkyHandler.getSolarEclipseHalfDuration()) { // fading out
                eclTick -= ConstellationSkyHandler.getSolarEclipseHalfDuration();
            } else {
                eclTick = ConstellationSkyHandler.getSolarEclipseHalfDuration() - eclTick;
            }
            float perc = ((float) eclTick) / ConstellationSkyHandler.getSolarEclipseHalfDuration();
            return prevBrightness * (0.05F + (0.95F * perc));
        }
        return prevBrightness;
    }

    @ASMCallHook
    @SideOnly(Side.CLIENT)
    public static float getSunBrightnessBodyInj(float prevBrightness, World world) {
        if (Config.weakSkyRendersWhitelist.contains(world.provider.getDimension())) {
            return prevBrightness;
        }
        WorldSkyHandler wsh = ConstellationSkyHandler.getInstance()
            .getWorldHandler(world);
        if (wsh != null && wsh.dayOfSolarEclipse && wsh.solarEclipse) {
            int eclTick = wsh.solarEclipseTick;
            if (eclTick >= ConstellationSkyHandler.getSolarEclipseHalfDuration()) { // fading out
                eclTick -= ConstellationSkyHandler.getSolarEclipseHalfDuration();
            } else {
                eclTick = ConstellationSkyHandler.getSolarEclipseHalfDuration() - eclTick;
            }
            float perc = ((float) eclTick) / ConstellationSkyHandler.getSolarEclipseHalfDuration();
            return prevBrightness * (0.05F + (0.95F * perc));
        }
        return prevBrightness;
    }

}
