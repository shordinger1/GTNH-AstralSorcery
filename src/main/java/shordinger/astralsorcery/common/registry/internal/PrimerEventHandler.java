/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.registry.internal;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.common.base.LightOreTransmutations;
import shordinger.astralsorcery.common.base.LiquidInteraction;
import shordinger.astralsorcery.common.base.WellLiquefaction;
import shordinger.astralsorcery.common.registry.*;
import shordinger.astralsorcery.common.registry.RegistryBlocks;
import shordinger.astralsorcery.common.registry.RegistryConstellations;
import shordinger.astralsorcery.common.registry.RegistryEnchantments;
import shordinger.astralsorcery.common.registry.RegistryItems;
import shordinger.astralsorcery.common.registry.RegistryPotions;
import shordinger.astralsorcery.common.registry.RegistryRecipes;
import shordinger.astralsorcery.common.registry.RegistrySounds;
import shordinger.astralsorcery.common.util.ASDataSerializers;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PrimerEventHandler
 * Created by HellFirePvP
 * Date: 26.06.2017 / 14:50
 */
public class PrimerEventHandler {

    private final InternalRegistryPrimer registry;

    public PrimerEventHandler(InternalRegistryPrimer registry) {
        this.registry = registry;
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        registry.wipe(event.getClass());
        RegistryItems.init();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
        AstralSorcery.proxy.registerOreDictEntries();
        RegistryConstellations.initConstellationSignatures();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        registry.wipe(event.getClass());
        RegistryBlocks.init();
        RegistryBlocks.initRenderRegistry();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    @SubscribeEvent
    public void registerBiomes(RegistryEvent.Register<Biome> event) {
        registry.wipe(event.getClass());
        // ? maybe. one day.
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        registry.wipe(event.getClass());
        RegistryPotions.init();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    @SubscribeEvent
    public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        registry.wipe(event.getClass());
        RegistryEnchantments.init();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        registry.wipe(event.getClass());
        RegistryRecipes.initVanillaRecipes();
        RegistryRecipes.initAstralRecipes();
        WellLiquefaction.init();
        LiquidInteraction.init();
        LightOreTransmutations.init();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        registry.wipe(event.getClass());
        RegistrySounds.init();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    @SubscribeEvent
    public void registerDataSerializers(RegistryEvent.Register<DataSerializerEntry> event) {
        registry.wipe(event.getClass());
        ASDataSerializers.registerSerializers();
        fillRegistry(
            event.getRegistry()
                .getRegistrySuperType(),
            event.getRegistry());
    }

    private <T extends IForgeRegistryEntry<T>> void fillRegistry(Class<T> registrySuperType,
                                                                 IForgeRegistry<T> forgeRegistry) {
        List<?> entries = registry.getEntries(registrySuperType);
        if (entries != null) {
            entries.forEach((e) -> forgeRegistry.register((T) e));
        }
    }

}
