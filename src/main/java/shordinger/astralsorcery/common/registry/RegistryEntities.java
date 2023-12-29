/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.entities.*;
import shordinger.astralsorcery.common.entities.EntityCrystal;
import shordinger.astralsorcery.common.entities.EntityCrystalTool;
import shordinger.astralsorcery.common.entities.EntityFlare;
import shordinger.astralsorcery.common.entities.EntityGrapplingHook;
import shordinger.astralsorcery.common.entities.EntityIlluminationSpark;
import shordinger.astralsorcery.common.entities.EntityItemExplosionResistant;
import shordinger.astralsorcery.common.entities.EntityItemHighlighted;
import shordinger.astralsorcery.common.entities.EntityItemStardust;
import shordinger.astralsorcery.common.entities.EntityLiquidSpark;
import shordinger.astralsorcery.common.entities.EntityNocturnalSpark;
import shordinger.astralsorcery.common.entities.EntityObservatoryHelper;
import shordinger.astralsorcery.common.entities.EntityShootingStar;
import shordinger.astralsorcery.common.entities.EntitySpectralTool;
import shordinger.astralsorcery.common.entities.EntityStarburst;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEntities
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:19
 */
public class RegistryEntities {

    public static void init() {
        registerEntities();
    }

    private static void registerEntities() {
        int modEid = 0;

        registerEntity(EntityItemHighlighted.class, "EntityHighlighted", modEid++, 64, 20, true);
        registerEntity(EntityItemStardust.class, "EntityStardust", modEid++, 64, 20, true);
        registerEntity(EntityCrystal.class, "EntityCrystal", modEid++, 64, 20, true);
        registerEntity(EntityFlare.class, "EntityFlare", modEid++, 64, 2, true);
        registerEntity(EntityStarburst.class, "EntityStarBurst", modEid++, 32, 1, true);
        registerEntity(EntityIlluminationSpark.class, "EntityIlluminationSpark", modEid++, 32, 1, true);
        registerEntity(EntityNocturnalSpark.class, "EntityNocturnalSpark", modEid++, 32, 1, true);
        registerEntity(EntityCrystalTool.class, "EntityCrystalTool", modEid++, 64, 20, true);
        registerEntity(EntityGrapplingHook.class, "EntityGrapplingHook", modEid++, 128, 1, true);
        registerEntity(EntitySpectralTool.class, "EntitySpectralTool", modEid++, 128, 1, true);
        registerEntity(EntityLiquidSpark.class, "EntityLiquidSpark", modEid++, 64, 1, true);
        registerEntity(EntityObservatoryHelper.class, "EntityObservatoryHelper", modEid++, 64, 1, true);
        // registerEntity(SpellProjectile.class, "EntitySpellProjectile", modEid++, 128, 1, true);
        registerEntity(EntityShootingStar.class, "EntityShootingStar", modEid++, 128, 1, true);
        registerEntity(EntityItemExplosionResistant.class, "EntityItemDamageResistant", modEid++, 64, 1, true);
    }

    // trackingRange refers x/z distance, not y.
    private static void registerEntity(Class<? extends Entity> entityClass, String name, int id, int trackingRange,
                                       int updateFreq, boolean sendVelUpdates) {
        EntityRegistry.registerModEntity(
            new ResourceLocation(AstralSorcery.MODID, name.toLowerCase()),
            entityClass,
            name,
            id,
            AstralSorcery.instance,
            trackingRange,
            updateFreq,
            sendVelUpdates);
    }

}
