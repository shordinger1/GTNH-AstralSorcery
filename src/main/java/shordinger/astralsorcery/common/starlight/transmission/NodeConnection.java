/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.starlight.transmission;

import javax.annotation.Nullable;

import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NodeConnection
 * Created by HellFirePvP
 * Date: 03.08.2016 / 23:11
 */
public record NodeConnection<T extends IPrismTransmissionNode>(T node, BlockPos to, boolean canConnect) {

    public NodeConnection(@Nullable T node, BlockPos to, boolean canConnect) {
        this.node = node;
        this.to = to;
        this.canConnect = canConnect;
    }

    @Override
    @Nullable
    public T node() {
        return node;
    }
}
