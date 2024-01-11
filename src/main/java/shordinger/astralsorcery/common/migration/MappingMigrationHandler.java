/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.migration;

import java.util.LinkedList;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.lib.ItemsAS;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.datafix.FixTypes;
import shordinger.wrapper.net.minecraft.util.datafix.IFixableData;
import shordinger.wrapper.net.minecraftforge.common.MinecraftForge;
import shordinger.wrapper.net.minecraftforge.common.util.ModFixs;
import shordinger.wrapper.net.minecraftforge.event.RegistryEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLCommonHandler;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MappingMigrationHandler
 * Created by HellFirePvP
 * Date: 03.07.2017 / 12:50
 */
public class MappingMigrationHandler {

    // this is not generified/abstracted yet due to lazyness and it's just 1 thing to migrate so........

    private static final int DATA_FIXER_VERSION = 1;
    private static final ResourceLocation ILLUMINATION_POWDER_KEY = new ResourceLocation(
        AstralSorcery.MODID,
        "itemilluminationpowder");

    private static LinkedList<String> migrationTileNames = new LinkedList<>();

    public static void init() {
        MappingMigrationHandler instance = new MappingMigrationHandler();

        MinecraftForge.EVENT_BUS.register(instance);

        ModFixs fixes = FMLCommonHandler.instance()
            .getDataFixer()
            .init(AstralSorcery.MODID, DATA_FIXER_VERSION);
        fixes.registerFix(FixTypes.BLOCK_ENTITY, new IFixableData() {

            @Override
            public int getFixVersion() {
                return 1;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                ResourceLocation tileId = new ResourceLocation(compound.getString("id"));

                if ("minecraft".equals(tileId.getResourceDomain())) {
                    if (migrationTileNames.contains(tileId.getResourcePath())) {
                        compound.setString(
                            "id",
                            new ResourceLocation(AstralSorcery.MODID, tileId.getResourcePath()).toString());
                    }
                }

                tileId = new ResourceLocation(compound.getString("id"));

                if ("astralsorcery:tileportalnode".equals(tileId.toString())) {
                    compound.setString("id", "astralsorcery:tilestructcontroller");
                }

                return compound;
            }
        });
    }

    @SubscribeEvent
    public void onMissingMapping(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings()) {
            if (mapping.key.equals(ILLUMINATION_POWDER_KEY)) {
                mapping.remap(ItemsAS.useableDust);
            }
        }
    }

    public static void listenTileMigration(String name) {
        migrationTileNames.add(name);
    }
}
