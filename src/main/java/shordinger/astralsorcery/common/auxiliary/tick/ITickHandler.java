/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.auxiliary.tick;

import java.util.EnumSet;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITickHandler
 * Created by HellFirePvP
 * Date: 04.08.2016 / 11:21
 */
public interface ITickHandler {

    public void tick(TickEvent.Type type, Object... context);

    /**
     * WORLD, context: world
     * SERVER, context:
     * CLIENT, context:
     * RENDER, context: pTicks
     * PLAYER, context: player, side
     */
    public EnumSet<TickEvent.Type> getHandledTypes();

    public boolean canFire(TickEvent.Phase phase);

    public String getName();

}
