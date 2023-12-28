/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.resource;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import shordinger.astralsorcery.AstralSorcery;
import shordinger.astralsorcery.client.sky.RenderSkybox;
import shordinger.astralsorcery.client.util.TexturePreloader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetLibrary
 * Created by HellFirePvP
 * Date: 09.08.2016 / 11:00
 */
public class AssetLibrary implements IResourceManagerReloadListener {

    public static AssetLibrary resReloadInstance = new AssetLibrary();
    public static boolean reloading = false;

    private static Map<AssetLoader.SubLocation, Map<String, BindableResource>> loadedTextures = new HashMap<>();

    private AssetLibrary() {
    }

    public static BindableResource loadTexture(AssetLoader.TextureLocation location, String name) {
        if (name.endsWith(".png")) {
            throw new IllegalArgumentException("Tried to loadTexture with appended .png from the AssetLibrary!");
        }
        Map<String, BindableResource> resources = loadedTextures.computeIfAbsent(location, l -> new HashMap<>());
        if (resources.containsKey(name)) {
            return resources.get(name);
        }
        BindableResource res = AssetLoader.load(AssetLoader.AssetLocation.TEXTURES, location, name, ".png");
        resources.put(name, res);
        return res;
    }

    @Override
    public void onResourceManagerReload(@Nullable IResourceManager resourceManager) {
        if (reloading) return;
        reloading = true;
        AstralSorcery.log.info("[AssetLibrary] Refreshing and Invalidating Resources");
        for (Map<String, BindableResource> map : loadedTextures.values()) {
            for (BindableResource res : map.values()) {
                res.invalidateAndReload(); // Massively unloading all textures.
            }
        }
        reloading = false;

        AstralSorcery.log.info("[AssetLibrary] Finished reloading, rebinding textures...");
        TexturePreloader.doPreloadRoutine();
        RenderSkybox.resetAstralSkybox();
        AstralSorcery.log.info("[AssetLibrary] Successfully reloaded and rebound library.");
    }

}
