package shordinger.wrapper.net.minecraft.client.renderer.block.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.BlockModelShapes;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureMap;
import shordinger.wrapper.net.minecraft.client.resources.IResourceManager;
import shordinger.wrapper.net.minecraft.client.resources.IResourceManagerReloadListener;
import shordinger.wrapper.net.minecraft.util.registry.IRegistry;

@SideOnly(Side.CLIENT)
public class ModelManager implements IResourceManagerReloadListener {

    private IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;
    private final TextureMap texMap;
    private final BlockModelShapes modelProvider;
    private IBakedModel defaultModel;

    public ModelManager(TextureMap textures) {
        this.texMap = textures;
        this.modelProvider = new BlockModelShapes(this);
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        net.minecraftforge.client.model.ModelLoader modelbakery = new net.minecraftforge.client.model.ModelLoader(
            resourceManager,
            this.texMap,
            this.modelProvider);
        this.modelRegistry = modelbakery.setupModelRegistry();
        this.defaultModel = this.modelRegistry.getObject(ModelBakery.MODEL_MISSING);
        net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.modelRegistry, modelbakery);
        this.modelProvider.reloadModels();
    }

    public IBakedModel getModel(ModelResourceLocation modelLocation) {
        if (modelLocation == null) {
            return this.defaultModel;
        } else {
            IBakedModel ibakedmodel = this.modelRegistry.getObject(modelLocation);
            return ibakedmodel == null ? this.defaultModel : ibakedmodel;
        }
    }

    public IBakedModel getMissingModel() {
        return this.defaultModel;
    }

    public TextureMap getTextureMap() {
        return this.texMap;
    }

    public BlockModelShapes getBlockModelShapes() {
        return this.modelProvider;
    }
}
