/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.resource;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import shordinger.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.texture.ITextureObject;
import shordinger.wrapper.net.minecraft.client.renderer.texture.SimpleTexture;
import shordinger.wrapper.net.minecraft.client.renderer.texture.TextureUtil;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BindableResource
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:50
 */
@SideOnly(Side.CLIENT)
public class BindableResource extends AbstractRenderableTexture.Full {

    private ITextureObject resource = null;
    private String path;

    public BindableResource(String path) {
        this.path = path;
        allocateGlId();
    }

    public String getPath() {
        return path;
    }

    public boolean isInitialized() {
        return resource != null;
    }

    public ITextureObject getResource() {
        return resource;
    }

    public SpriteSheetResource asSpriteSheet(int rows, int columns) {
        return new SpriteSheetResource(this, rows, columns);
    }

    @Deprecated
    public void invalidateAndReload() {
        if (resource != null) GL11.glDeleteTextures(resource.getGlTextureId());
        resource = null;
    }

    public void allocateGlId() {
        if (resource != null || AssetLibrary.reloading) return;
        resource = new SimpleTexture(new ResourceLocation(path));
        try {
            resource.loadTexture(
                Minecraft.getMinecraft()
                    .getResourceManager());
        } catch (Exception exc) {
            AstralSorcery.log.warn("[AssetLibrary] Failed to load texture " + path);
            AstralSorcery.log.warn(
                "[AssetLibrary] Please report this issue; include the message above, the following stacktrace as well as instructions on how to reproduce this!");
            exc.printStackTrace();
            resource = TextureUtil.MISSING_TEXTURE;
            return;
        }
        if (AstralSorcery.isRunningInDevEnvironment()) {
            AstralSorcery.log.info("[AssetLibrary] Allocated " + path + " to " + resource.getGlTextureId());
        }
    }

    @Override
    public void bindTexture() {
        bind();
    }

    public void bind() {
        if (AssetLibrary.reloading) return; // we do nothing but wait.
        if (resource == null) {
            allocateGlId();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, resource.getGlTextureId());
        // GlStateManager.bindTexture(resource.getGlTextureId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BindableResource that = (BindableResource) obj;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
