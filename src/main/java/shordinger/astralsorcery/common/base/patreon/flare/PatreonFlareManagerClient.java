/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.base.patreon.flare;

import java.util.Collection;
import java.util.EnumSet;

import cpw.mods.fml.relauncher.Side;
import shordinger.astralsorcery.common.auxiliary.tick.ITickHandler;
import shordinger.astralsorcery.common.base.patreon.PatreonEffectHelper;
import shordinger.astralsorcery.common.base.patreon.base.PtEffectFixedSprite;
import shordinger.astralsorcery.common.data.DataPatreonFlares;
import shordinger.astralsorcery.common.data.SyncDataHolder;
import shordinger.astralsorcery.common.data.config.Config;
import shordinger.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonFlareManagerClient
 * Created by HellFirePvP
 * Date: 23.06.2018 / 17:44
 */
public class PatreonFlareManagerClient implements ITickHandler {

    public static PatreonFlareManagerClient INSTANCE = new PatreonFlareManagerClient();

    private PatreonFlareManagerClient() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World clWorld = Minecraft.getMinecraft().theWorld;
        EntityPlayer thisPlayer = Minecraft.getMinecraft().thePlayer;
        if (clWorld == null || thisPlayer == null) return;
        int clDim = clWorld.provider.getDimension();
        Vector3 thisPlayerPos = Vector3.atEntityCenter(thisPlayer);

        DataPatreonFlares dataFlares = SyncDataHolder.getDataClient(SyncDataHolder.DATA_PATREON_FLARES);
        for (Collection<PatreonPartialEntity> playerFlares : dataFlares.getEntities(Side.CLIENT)) {
            for (PatreonPartialEntity flare : playerFlares) {
                if (flare.getLastTickedDim() == null || clDim != flare.getLastTickedDim()) continue;
                if (flare.getPos()
                    .distanceSquared(thisPlayerPos) <= Config.maxEffectRenderDistanceSq) {
                    flare.tickInRenderDistance();
                }
                flare.update(clWorld);
            }
        }

        for (EntityPlayer pl : clWorld.playerEntities) {
            for (PatreonEffectHelper.PatreonEffect eff : PatreonEffectHelper
                .getPatreonEffects(Side.CLIENT, pl.getUniqueID())) {
                if (eff instanceof PtEffectFixedSprite) {
                    ((PtEffectFixedSprite) eff).doEffect(pl);
                }
            }
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Patreon Flare Manager (Client)";
    }
}
