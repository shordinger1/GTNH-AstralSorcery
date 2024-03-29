package shordinger.wrapper.net.minecraft.client.shader;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.OpenGlHelper;
import shordinger.wrapper.net.minecraft.client.resources.IResource;
import shordinger.wrapper.net.minecraft.client.resources.IResourceManager;
import shordinger.wrapper.net.minecraft.client.util.JsonException;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ShaderLoader {

    private final ShaderLoader.ShaderType shaderType;
    private final String shaderFilename;
    private final int shader;
    private int shaderAttachCount;

    private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename) {
        this.shaderType = type;
        this.shader = shaderId;
        this.shaderFilename = filename;
    }

    public void attachShader(ShaderManager manager) {
        ++this.shaderAttachCount;
        OpenGlHelper.glAttachShader(manager.getProgram(), this.shader);
    }

    public void deleteShader(ShaderManager manager) {
        --this.shaderAttachCount;

        if (this.shaderAttachCount <= 0) {
            OpenGlHelper.glDeleteShader(this.shader);
            this.shaderType.getLoadedShaders()
                .remove(this.shaderFilename);
        }
    }

    public String getShaderFilename() {
        return this.shaderFilename;
    }

    public static ShaderLoader loadShader(IResourceManager resourceManager, ShaderLoader.ShaderType type,
                                          String filename) throws IOException {
        ShaderLoader shaderloader = (ShaderLoader) type.getLoadedShaders()
            .get(filename);

        if (shaderloader == null) {
            String[] rl = ResourceLocation.splitObjectName(filename);
            ResourceLocation resourcelocation = new ResourceLocation(
                rl[0],
                "shaders/program/" + rl[1] + type.getShaderExtension());
            IResource iresource = resourceManager.getResource(resourcelocation);

            try {
                byte[] abyte = IOUtils.toByteArray(new BufferedInputStream(iresource.getInputStream()));
                ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
                bytebuffer.put(abyte);
                bytebuffer.position(0);
                int i = OpenGlHelper.glCreateShader(type.getShaderMode());
                OpenGlHelper.glShaderSource(i, bytebuffer);
                OpenGlHelper.glCompileShader(i);

                if (OpenGlHelper.glGetShaderi(i, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
                    String s = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(i, 32768));
                    JsonException jsonexception = new JsonException(
                        "Couldn't compile " + type.getShaderName() + " program: " + s);
                    jsonexception.setFilenameAndFlush(resourcelocation.getResourcePath());
                    throw jsonexception;
                }

                shaderloader = new ShaderLoader(type, i, filename);
                type.getLoadedShaders()
                    .put(filename, shaderloader);
            } finally {
                IOUtils.closeQuietly((Closeable) iresource);
            }
        }

        return shaderloader;
    }

    @SideOnly(Side.CLIENT)
    public static enum ShaderType {

        VERTEX("vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

        private final String shaderName;
        private final String shaderExtension;
        private final int shaderMode;
        private final Map<String, ShaderLoader> loadedShaders = Maps.<String, ShaderLoader>newHashMap();

        private ShaderType(String shaderNameIn, String shaderExtensionIn, int shaderModeIn) {
            this.shaderName = shaderNameIn;
            this.shaderExtension = shaderExtensionIn;
            this.shaderMode = shaderModeIn;
        }

        public String getShaderName() {
            return this.shaderName;
        }

        private String getShaderExtension() {
            return this.shaderExtension;
        }

        private int getShaderMode() {
            return this.shaderMode;
        }

        /**
         * gets a map of loaded shaders for the ShaderType.
         */
        private Map<String, ShaderLoader> getLoadedShaders() {
            return this.loadedShaders;
        }
    }
}
