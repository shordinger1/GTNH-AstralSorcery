/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.base;

import shordinger.astralsorcery.client.ClientScheduler;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.entity.PartialEntityFlareColor;
import shordinger.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;
import shordinger.astralsorcery.common.util.Provider;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.Side;
import shordinger.wrapper.net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PtEffectDynColorFlare
 * Created by HellFirePvP
 * Date: 04.01.2019 / 21:22
 */
public class PtEffectDynColorFlare extends PatreonEffectHelper.PatreonEffect {

    private Provider<Color> colorProvider;

    public PtEffectDynColorFlare(UUID uniqueId, Provider<Color> colorProvider) {
        super(uniqueId, null);
        this.colorProvider = colorProvider;
    }

    @SideOnly(Side.CLIENT)
    public static long getClientTick() {
        return ClientScheduler.getClientTick();
    }

    @Override
    public boolean hasPartialEntity() {
        return true;
    }

    @Nullable
    @Override
    public PatreonPartialEntity createEntity(UUID playerUUID) {
        return new PartialEntityFlareColor(playerUUID, colorProvider);
    }
}
