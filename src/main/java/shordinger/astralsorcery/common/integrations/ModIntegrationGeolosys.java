/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Optional;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import shordinger.astralsorcery.client.util.item.ItemRenderRegistry;
import shordinger.astralsorcery.common.integrations.mods.geolosys.BlockGeolosysSampleCluster;
import shordinger.astralsorcery.common.integrations.mods.geolosys.TESRGeolosysSampleCluster;
import shordinger.astralsorcery.common.integrations.mods.geolosys.TileGeolosysSampleCluster;
import shordinger.astralsorcery.common.registry.RegistryBlocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationGeolosys
 * Created by HellFirePvP
 * Date: 03.10.2017 / 17:21
 */
public class ModIntegrationGeolosys {

    public static Block geolosysSample;

    public static void registerGeolosysSampleBlock() {
        geolosysSample = RegistryBlocks.registerBlock(new BlockGeolosysSampleCluster());
        RegistryBlocks.queueDefaultItemBlock(geolosysSample);
        RegistryBlocks.registerTile(TileGeolosysSampleCluster.class);
    }

    @SideOnly(Side.CLIENT)
    public static void registerGeolosysSampleRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGeolosysSampleCluster.class, new TESRGeolosysSampleCluster());
    }

    @SideOnly(Side.CLIENT)
    public static void registerGeolosysSampleItemRenderer() {
        ItemRenderRegistry.register(Item.getItemFromBlock(geolosysSample), new TESRGeolosysSampleCluster());
    }

    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "jei")
    public static void hideJEIGeolosysSample(IIngredientBlacklist blacklist) {
        blacklist.addIngredientToBlacklist(new ItemStack(geolosysSample));
    }

}
