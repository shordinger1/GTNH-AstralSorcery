package shordinger.wrapper.net.minecraft.client.renderer.block.model;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureAtlasSprite;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.WeightedRandom;

@SideOnly(Side.CLIENT)
public class WeightedBakedModel implements IBakedModel {

    private final int totalWeight;
    private final List<WeightedBakedModel.WeightedModel> models;
    public final IBakedModel baseModel;

    public WeightedBakedModel(List<WeightedBakedModel.WeightedModel> modelsIn) {
        this.models = modelsIn;
        this.totalWeight = WeightedRandom.getTotalWeight(modelsIn);
        this.baseModel = (modelsIn.get(0)).model;
    }

    public IBakedModel getRandomModel(long p_188627_1_) {
        return ((WeightedBakedModel.WeightedModel) WeightedRandom
            .getRandomItem(this.models, Math.abs((int) p_188627_1_ >> 16) % this.totalWeight)).model;
    }

    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return this.getRandomModel(rand)
            .getQuads(state, side, rand);
    }

    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    public boolean isAmbientOcclusion(IBlockState state) {
        return this.baseModel.isAmbientOcclusion(state);
    }

    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return this.baseModel.getItemCameraTransforms();
    }

    public ItemOverrideList getOverrides() {
        return this.baseModel.getOverrides();
    }

    @SideOnly(Side.CLIENT)
    public static class Builder {

        private final List<WeightedBakedModel.WeightedModel> listItems = Lists.<WeightedBakedModel.WeightedModel>newArrayList();

        public WeightedBakedModel.Builder add(IBakedModel model, int weight) {
            this.listItems.add(new WeightedBakedModel.WeightedModel(model, weight));
            return this;
        }

        public WeightedBakedModel build() {
            Collections.sort(this.listItems);
            return new WeightedBakedModel(this.listItems);
        }

        public IBakedModel first() {
            return (this.listItems.get(0)).model;
        }
    }

    @SideOnly(Side.CLIENT)
    static class WeightedModel extends WeightedRandom.Item implements Comparable<WeightedBakedModel.WeightedModel> {

        protected final IBakedModel model;

        public WeightedModel(IBakedModel modelIn, int itemWeightIn) {
            super(itemWeightIn);
            this.model = modelIn;
        }

        public int compareTo(WeightedBakedModel.WeightedModel p_compareTo_1_) {
            return ComparisonChain.start()
                .compare(p_compareTo_1_.itemWeight, this.itemWeight)
                .result();
        }

        public String toString() {
            return "MyWeighedRandomItem{weight=" + this.itemWeight + ", model=" + this.model + '}';
        }
    }
}
