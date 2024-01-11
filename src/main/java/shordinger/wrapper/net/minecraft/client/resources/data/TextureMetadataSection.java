package shordinger.wrapper.net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureMetadataSection implements IMetadataSection {

    private final boolean textureBlur;
    private final boolean textureClamp;

    public TextureMetadataSection(boolean textureBlurIn, boolean textureClampIn) {
        this.textureBlur = textureBlurIn;
        this.textureClamp = textureClampIn;
    }

    public boolean getTextureBlur() {
        return this.textureBlur;
    }

    public boolean getTextureClamp() {
        return this.textureClamp;
    }
}
