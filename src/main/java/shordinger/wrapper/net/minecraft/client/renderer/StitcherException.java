package shordinger.wrapper.net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.texture.Stitcher;

@SideOnly(Side.CLIENT)
public class StitcherException extends RuntimeException {

    private final Stitcher.Holder holder;

    public StitcherException(Stitcher.Holder holderIn, String message) {
        super(message);
        this.holder = holderIn;
    }
}
