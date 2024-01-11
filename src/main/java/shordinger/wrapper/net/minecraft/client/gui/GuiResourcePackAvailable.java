package shordinger.wrapper.net.minecraft.client.gui;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.client.resources.ResourcePackListEntry;

@SideOnly(Side.CLIENT)
public class GuiResourcePackAvailable extends GuiResourcePackList {

    public GuiResourcePackAvailable(Minecraft mcIn, int p_i45054_2_, int p_i45054_3_,
                                    List<ResourcePackListEntry> p_i45054_4_) {
        super(mcIn, p_i45054_2_, p_i45054_3_, p_i45054_4_);
    }

    protected String getListHeader() {
        return I18n.format("resourcePack.available.title");
    }
}
