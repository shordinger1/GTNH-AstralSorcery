/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.world.Teleporter;
import shordinger.wrapper.net.minecraft.world.WorldServer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NoOpTeleporter
 * Created by HellFirePvP
 * Date: 19.04.2017 / 14:37
 */
public class NoOpTeleporter extends Teleporter {

    public NoOpTeleporter(WorldServer worldIn) {
        super(worldIn);
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {}

    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        return true;
    }

    @Override
    public boolean makePortal(Entity entityIn) {
        return true;
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {}

}
