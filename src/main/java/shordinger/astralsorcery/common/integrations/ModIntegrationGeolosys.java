/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.integrations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import shordinger.astralsorcery.client.util.item.ItemRenderRegistry;
import shordinger.astralsorcery.common.integrations.mods.geolosys.BlockGeolosysSampleCluster;
import shordinger.astralsorcery.common.integrations.mods.geolosys.TESRGeolosysSampleCluster;
import shordinger.astralsorcery.common.integrations.mods.geolosys.TileGeolosysSampleCluster;
import shordinger.astralsorcery.common.registry.RegistryBlocks;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraftforge.fml.client.registry.ClientRegistry;
import shordinger.wrapper.net.minecraftforge.fml.common.Optional;

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
