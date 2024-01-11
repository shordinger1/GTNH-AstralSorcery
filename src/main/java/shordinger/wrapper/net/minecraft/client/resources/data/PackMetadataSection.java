package shordinger.wrapper.net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

@SideOnly(Side.CLIENT)
public class PackMetadataSection implements IMetadataSection {

    private final ITextComponent packDescription;
    private final int packFormat;

    public PackMetadataSection(ITextComponent packDescriptionIn, int packFormatIn) {
        this.packDescription = packDescriptionIn;
        this.packFormat = packFormatIn;
    }

    public ITextComponent getPackDescription() {
        return this.packDescription;
    }

    public int getPackFormat() {
        return this.packFormat;
    }
}
