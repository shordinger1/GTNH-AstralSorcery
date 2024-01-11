package shordinger.wrapper.net.minecraft.client.renderer.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITextureMapPopulator {

    void registerSprites(TextureMap textureMapIn);
}
