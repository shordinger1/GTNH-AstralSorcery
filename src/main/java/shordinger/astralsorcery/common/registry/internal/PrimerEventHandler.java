/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.internal;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.base.LightOreTransmutations;
import shordinger.astralsorcery.common.base.LiquidInteraction;
import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.registry.*;
import shordinger.astralsorcery.common.util.ASDataSerializers;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.enchantment.Enchantment;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.crafting.IRecipe;
import shordinger.wrapper.net.minecraft.potion.Potion;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.world.biome.Biome;
import shordinger.wrapper.net.minecraftforge.event.RegistryEvent;
import shordinger.wrapper.net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import shordinger.wrapper.net.minecraftforge.registries.DataSerializerEntry;
import shordinger.wrapper.net.minecraftforge.registries.IForgeRegistry;
import shordinger.wrapper.net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrimerEventHandler
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:50
 */
public class PrimerEventHandler {

    private InternalRegistryPrimer registry;

    public PrimerEventHandler(InternalRegistryPrimer registry) {
        this.registry = registry;
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        registry.wipe(event.getClass());
        RegistryItems.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
        AstralSorcery.proxy.registerOreDictEntries();
        RegistryConstellations.initConstellationSignatures();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        registry.wipe(event.getClass());
        RegistryBlocks.init();
        RegistryBlocks.initRenderRegistry();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        registry.wipe(event.getClass());
        //? maybe. one day.
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        registry.wipe(event.getClass());
        RegistryPotions.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        registry.wipe(event.getClass());
        RegistryEnchantments.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        registry.wipe(event.getClass());
        RegistryRecipes.initVanillaRecipes();
        RegistryRecipes.initAstralRecipes();
        WellLiquefaction.init();
        LiquidInteraction.init();
        LightOreTransmutations.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        registry.wipe(event.getClass());
        RegistrySounds.init();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    @SubscribeEvent
    public void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
        registry.wipe(event.getClass());
        ASDataSerializers.registerSerializers();
        fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType, IForgeRegistry<T> forgeRegistry) {
        List<?> entries = registry.getEntries(registrySuperType);
        if(entries != null) {
            entries.forEach((e) -> forgeRegistry.register((T) e));
        }
    }

}
