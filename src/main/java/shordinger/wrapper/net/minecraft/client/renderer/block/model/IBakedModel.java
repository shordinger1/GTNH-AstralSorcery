package shordinger.wrapper.net.minecraft.client.renderer.block.model;

import java.util.List;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureAtlasSprite;
import shordinger.wrapper.net.minecraft.util.EnumFacing;

@SideOnly(Side.CLIENT)
public interface IBakedModel {

    List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand);

    boolean isAmbientOcclusion();

    boolean isGui3d();

    boolean isBuiltInRenderer();

    TextureAtlasSprite getParticleTexture();

    @Deprecated
    default ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    ItemOverrideList getOverrides();

    default boolean isAmbientOcclusion(IBlockState state) {
        return isAmbientOcclusion();
    }

    /*
     * Returns the pair of the model for the given perspective, and the matrix
     * that should be applied to the GL state before rendering it (matrix may be null).
     */
    default org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(
        ItemCameraTransforms.TransformType cameraTransformType) {
        return net.minecraftforge.client.ForgeHooksClient.handlePerspective(this, cameraTransformType);
    }
}
