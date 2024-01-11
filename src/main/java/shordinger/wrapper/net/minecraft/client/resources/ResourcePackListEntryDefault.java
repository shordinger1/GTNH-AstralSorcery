package shordinger.wrapper.net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreenResourcePacks;

@SideOnly(Side.CLIENT)
public class ResourcePackListEntryDefault extends ResourcePackListEntryServer {

    public ResourcePackListEntryDefault(GuiScreenResourcePacks resourcePacksGUIIn) {
        super(
            resourcePacksGUIIn,
            Minecraft.getMinecraft()
                .getResourcePackRepository().rprDefaultResourcePack);
    }

    protected String getResourcePackName() {
        return "Default";
    }

    public boolean isServerPack() {
        return false;
    }
}
