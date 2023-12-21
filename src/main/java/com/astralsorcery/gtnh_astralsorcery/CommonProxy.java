package com.astralsorcery.gtnh_astralsorcery;

import static com.astralsorcery.gtnh_astralsorcery.AstralSorcery.LOG;

import com.astralsorcery.gtnh_astralsorcery.common.block.AstralBlockRegistry;
import com.astralsorcery.gtnh_astralsorcery.common.item.AstralItemRegistry;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        AstralItemRegistry.load();
        AstralBlockRegistry.load();
        LOG.info(Config.greeting);
        LOG.info("I am " + Tags.MODNAME + " at version " + Tags.VERSION);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {}

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {

        // ItemList.items = items;
        // ItemList.itemMap = itemMap;

    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {}

}
