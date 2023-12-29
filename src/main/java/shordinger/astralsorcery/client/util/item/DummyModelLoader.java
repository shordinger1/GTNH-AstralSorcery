/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.item;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DummyModelLoader
 * Created by HellFirePvP
 * Date: 23.07.2016 / 16:17
 */
public class DummyModelLoader implements ICustomModelLoader {

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return ItemRenderRegistry.isRegistered(modelLocation);
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new ItemRendererModelDummy(modelLocation);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    @Override
    public String toString() {
        return "IItemRenderer-DummyModelLoader";
    }
}
