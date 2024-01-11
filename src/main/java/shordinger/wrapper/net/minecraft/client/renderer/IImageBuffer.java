package shordinger.wrapper.net.minecraft.client.renderer;

import java.awt.image.BufferedImage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IImageBuffer {

    BufferedImage parseUserSkin(BufferedImage image);

    void skinAvailable();
}
