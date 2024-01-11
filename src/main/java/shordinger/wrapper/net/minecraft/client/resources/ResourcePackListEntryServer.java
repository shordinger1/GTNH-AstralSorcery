package shordinger.wrapper.net.minecraft.client.resources;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreenResourcePacks;
import shordinger.wrapper.net.minecraft.client.renderer.texture.DynamicTexture;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureUtil;
import shordinger.wrapper.net.minecraft.client.resources.data.PackMetadataSection;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;

@SideOnly(Side.CLIENT)
public class ResourcePackListEntryServer extends ResourcePackListEntry {

    private static final Logger LOGGER = LogManager.getLogger();
    private final IResourcePack resourcePack;
    private final ResourceLocation resourcePackIcon;

    public ResourcePackListEntryServer(GuiScreenResourcePacks resourcePacksGUIIn, IResourcePack resourcePackIn) {
        super(resourcePacksGUIIn);
        this.resourcePack = resourcePackIn;
        DynamicTexture dynamictexture;

        try {
            dynamictexture = new DynamicTexture(resourcePackIn.getPackImage());
        } catch (IOException var5) {
            dynamictexture = TextureUtil.MISSING_TEXTURE;
        }

        this.resourcePackIcon = this.mc.getTextureManager()
            .getDynamicTextureLocation("texturepackicon", dynamictexture);
    }

    protected int getResourcePackFormat() {
        return 3;
    }

    protected String getResourcePackDescription() {
        try {
            PackMetadataSection packmetadatasection = (PackMetadataSection) this.resourcePack
                .getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");

            if (packmetadatasection != null) {
                return packmetadatasection.getPackDescription()
                    .getFormattedText();
            }
        } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't load metadata info", (Throwable) jsonparseexception);
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't load metadata info", (Throwable) ioexception);
        }

        return TextFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
    }

    protected boolean canMoveRight() {
        return false;
    }

    protected boolean canMoveLeft() {
        return false;
    }

    protected boolean canMoveUp() {
        return false;
    }

    protected boolean canMoveDown() {
        return false;
    }

    protected String getResourcePackName() {
        return "Server";
    }

    protected void bindResourcePackIcon() {
        this.mc.getTextureManager()
            .bindTexture(this.resourcePackIcon);
    }

    protected boolean showHoverOverlay() {
        return false;
    }

    public boolean isServerPack() {
        return true;
    }
}
