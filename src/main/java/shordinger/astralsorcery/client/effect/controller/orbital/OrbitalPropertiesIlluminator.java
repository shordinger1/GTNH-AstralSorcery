/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.effect.controller.orbital;

import java.awt.*;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import shordinger.astralsorcery.client.effect.EffectHelper;
import shordinger.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import shordinger.astralsorcery.common.lib.BlocksAS;
import shordinger.astralsorcery.common.tile.TileIlluminator;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.astralsorcery.migration.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalPropertiesIlluminator
 * Created by HellFirePvP
 * Date: 02.11.2016 / 00:20
 */
public class OrbitalPropertiesIlluminator
    implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect {

    private static final Random rand = new Random();

    private final BlockPos thisPos;
    private final int dim;

    public OrbitalPropertiesIlluminator(TileIlluminator tile) {
        this.thisPos = tile.getPos();
        this.dim = tile.getWorld().provider.dimensionId;
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        World w = Minecraft.getMinecraft().theWorld;
        return w.provider.dimensionId == dim && w.getBlockState(thisPos)
            .getBlock()
            .equals(BlocksAS.blockIlluminator);
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if (!Minecraft.isFancyGraphicsEnabled()) return;
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
        p.setMaxAge(25);
        switch (rand.nextInt(3)) {
            case 0 -> p.setColor(Color.WHITE);
            case 1 -> p.setColor(new Color(0xFEFF9E));
            case 2 -> p.setColor(new Color(0xFFE539));
            default -> {
            }
        }
        p.scale(0.1F)
            .gravity(0.004);
        if (rand.nextInt(4) == 0) {
            p = EffectHelper.genericFlareParticle(pos.getX(), pos.getY(), pos.getZ());
            p.motion(
                (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
            p.setMaxAge(35);
            switch (rand.nextInt(2)) {
                case 0 -> p.setColor(new Color(0xFEFF9E));
                case 1 -> p.setColor(new Color(0xFFE539));
                default -> {
                }
            }
            p.scale(0.15F);
        }
        /*
         * if(rand.nextBoolean()) {
         * p = EffectHelper.genericFlareParticle(
         * pos.getX(),
         * pos.getY(),
         * pos.getZ());
         * p.motion((rand.nextFloat() * 0.002F) * (rand.nextBoolean() ? 1 : -1),
         * (rand.nextFloat() * 0.002F) * (rand.nextBoolean() ? 1 : -1),
         * (rand.nextFloat() * 0.002F) * (rand.nextBoolean() ? 1 : -1));
         * p.setMaxAge(5);
         * p.scale(0.15F);
         * }
         */
    }

}
