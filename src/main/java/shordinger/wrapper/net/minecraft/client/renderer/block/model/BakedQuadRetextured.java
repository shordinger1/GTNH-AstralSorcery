package shordinger.wrapper.net.minecraft.client.renderer.block.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class BakedQuadRetextured extends BakedQuad {

    private final TextureAtlasSprite texture;

    public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(
            Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length),
            quad.tintIndex,
            FaceBakery.getFacingFromVertexData(quad.getVertexData()),
            quad.getSprite(),
            quad.applyDiffuseLighting,
            quad.format);
        this.texture = textureIn;
        this.remapQuad();
    }

    private void remapQuad() {
        for (int i = 0; i < 4; ++i) {
            int j = format.getIntegerSize() * i;
            int uvIndex = format.getUvOffsetById(0) / 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(
                this.texture.getInterpolatedU(
                    (double) this.sprite.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(
                this.texture.getInterpolatedV(
                    (double) this.sprite.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
        }
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}
