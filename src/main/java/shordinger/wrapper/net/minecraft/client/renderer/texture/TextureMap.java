package shordinger.wrapper.net.minecraft.client.renderer.texture;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.renderer.StitcherException;
import shordinger.wrapper.net.minecraft.client.resources.IResource;
import shordinger.wrapper.net.minecraft.client.resources.IResourceManager;
import shordinger.wrapper.net.minecraft.crash.CrashReport;
import shordinger.wrapper.net.minecraft.crash.CrashReportCategory;
import shordinger.wrapper.net.minecraft.crash.ICrashReportDetail;
import shordinger.wrapper.net.minecraft.util.ReportedException;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;

@SideOnly(Side.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
    public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
    private final List<TextureAtlasSprite> listAnimatedSprites;
    private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
    public final Map<String, TextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final ITextureMapPopulator iconCreator;
    private int mipmapLevels;
    private final TextureAtlasSprite missingImage;

    public TextureMap(String basePathIn) {
        this(basePathIn, (ITextureMapPopulator) null);
    }

    public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn) {
        this(basePathIn, iconCreatorIn, false);
    }

    public TextureMap(String basePathIn, boolean skipFirst) {
        this(basePathIn, null, skipFirst);
    }

    public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn, boolean skipFirst) {
        this.listAnimatedSprites = Lists.<TextureAtlasSprite>newArrayList();
        this.mapRegisteredSprites = Maps.<String, TextureAtlasSprite>newHashMap();
        this.mapUploadedSprites = Maps.<String, TextureAtlasSprite>newHashMap();
        this.missingImage = new TextureAtlasSprite("missingno");
        this.basePath = basePathIn;
        this.iconCreator = iconCreatorIn;
    }

    private void initMissingImage() {
        int[] aint = TextureUtil.MISSING_TEXTURE_DATA;
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
        int[][] aint1 = new int[this.mipmapLevels + 1][];
        aint1[0] = aint;
        this.missingImage.setFramesTextureData(Lists.<int[][]>newArrayList(aint1));
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.iconCreator != null) {
            this.loadSprites(resourceManager, this.iconCreator);
        }
    }

    public void loadSprites(IResourceManager resourceManager, ITextureMapPopulator iconCreatorIn) {
        this.mapRegisteredSprites.clear();
        net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this);
        iconCreatorIn.registerSprites(this);
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(resourceManager);
    }

    public void loadTextureAtlas(IResourceManager resourceManager) {
        int i = Minecraft.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int j = Integer.MAX_VALUE;
        int k = 1 << this.mipmapLevels;
        net.minecraftforge.fml.common.FMLLog.log.info("Max texture size: {}", i);
        net.minecraftforge.fml.common.ProgressManager.ProgressBar bar = net.minecraftforge.fml.common.ProgressManager
            .push("Texture stitching", this.mapRegisteredSprites.size());
        loadedSprites.clear();

        for (Entry<String, TextureAtlasSprite> entry : Maps.newHashMap(this.mapRegisteredSprites)
            .entrySet()) {
            final ResourceLocation location = new ResourceLocation(entry.getKey());
            bar.step(location.toString());
            j = loadTexture(stitcher, resourceManager, location, entry.getValue(), bar, j, k);
        }
        finishLoading(stitcher, bar, j, k);
    }

    private int loadTexture(Stitcher stitcher, IResourceManager resourceManager, ResourceLocation location,
                            TextureAtlasSprite textureatlassprite, net.minecraftforge.fml.common.ProgressManager.ProgressBar bar, int j,
                            int k) {
        if (loadedSprites.contains(location)) {
            return j;
        }
        ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);
        IResource iresource = null;

        for (ResourceLocation loading : loadingSprites) {
            if (location.equals(loading)) {
                final String error = "circular texture dependencies, stack: [" + com.google.common.base.Joiner.on(", ")
                    .join(loadingSprites) + "]";
                net.minecraftforge.fml.client.FMLClientHandler.instance()
                    .trackBrokenTexture(resourcelocation, error);
                return j;
            }
        }
        loadingSprites.addLast(location);
        try {
            for (ResourceLocation dependency : textureatlassprite.getDependencies()) {
                if (!mapRegisteredSprites.containsKey(dependency.toString())) {
                    registerSprite(dependency);
                }
                TextureAtlasSprite depSprite = mapRegisteredSprites.get(dependency.toString());
                j = loadTexture(stitcher, resourceManager, dependency, depSprite, bar, j, k);
            }
            try {
                if (textureatlassprite.hasCustomLoader(resourceManager, resourcelocation)) {
                    if (textureatlassprite
                        .load(resourceManager, resourcelocation, l -> mapRegisteredSprites.get(l.toString()))) {
                        return j;
                    }
                } else {
                    PngSizeInfo pngsizeinfo = PngSizeInfo
                        .makeFromResource(resourceManager.getResource(resourcelocation));
                    iresource = resourceManager.getResource(resourcelocation);
                    boolean flag = iresource.getMetadata("animation") != null;
                    textureatlassprite.loadSprite(pngsizeinfo, flag);
                }
            } catch (RuntimeException runtimeexception) {
                net.minecraftforge.fml.client.FMLClientHandler.instance()
                    .trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
                return j;
            } catch (IOException ioexception) {
                net.minecraftforge.fml.client.FMLClientHandler.instance()
                    .trackMissingTexture(resourcelocation);
                return j;
            } finally {
                IOUtils.closeQuietly((Closeable) iresource);
            }

            j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
            int j1 = Math.min(
                Integer.lowestOneBit(textureatlassprite.getIconWidth()),
                Integer.lowestOneBit(textureatlassprite.getIconHeight()));

            if (j1 < k) {
                // FORGE: do not lower the mipmap level, just log the problematic textures
                LOGGER.warn(
                    "Texture {} with size {}x{} will have visual artifacts at mip level {}, it can only support level {}. Please report to the mod author that the texture should be some multiple of 16x16.",
                    resourcelocation,
                    Integer.valueOf(textureatlassprite.getIconWidth()),
                    Integer.valueOf(textureatlassprite.getIconHeight()),
                    Integer.valueOf(MathHelper.log2(k)),
                    Integer.valueOf(MathHelper.log2(j1)));
            }

            if (generateMipmaps(resourceManager, textureatlassprite)) stitcher.addSprite(textureatlassprite);
            return j;
        } finally {
            loadingSprites.removeLast();
            loadedSprites.add(location);
        }
    }

    private void finishLoading(Stitcher stitcher, net.minecraftforge.fml.common.ProgressManager.ProgressBar bar, int j,
                               int k) {
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        int l = Math.min(j, k);
        int i1 = MathHelper.log2(l);

        if (false) // FORGE: do not lower the mipmap level
            if (i1 < this.mipmapLevels) {
                LOGGER.warn(
                    "{}: dropping miplevel from {} to {}, because of minimum power of two: {}",
                    this.basePath,
                    Integer.valueOf(this.mipmapLevels),
                    Integer.valueOf(i1),
                    Integer.valueOf(l));
                this.mipmapLevels = i1;
            }

        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);
        bar = net.minecraftforge.fml.common.ProgressManager.push("Texture creation", 2);

        try {
            bar.step("Stitching");
            stitcher.doStitch();
        } catch (StitcherException stitcherexception) {
            throw stitcherexception;
        }

        LOGGER.info(
            "Created: {}x{} {}-atlas",
            Integer.valueOf(stitcher.getCurrentWidth()),
            Integer.valueOf(stitcher.getCurrentHeight()),
            this.basePath);
        bar.step("Allocating GL texture");
        TextureUtil.allocateTextureImpl(
            this.getGlTextureId(),
            this.mipmapLevels,
            stitcher.getCurrentWidth(),
            stitcher.getCurrentHeight());
        Map<String, TextureAtlasSprite> map = Maps.<String, TextureAtlasSprite>newHashMap(this.mapRegisteredSprites);

        net.minecraftforge.fml.common.ProgressManager.pop(bar);
        bar = net.minecraftforge.fml.common.ProgressManager.push(
            "Texture mipmap and upload",
            stitcher.getStichSlots()
                .size());

        for (TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
            bar.step(textureatlassprite1.getIconName());
            {
                String s = textureatlassprite1.getIconName();
                map.remove(s);
                this.mapUploadedSprites.put(s, textureatlassprite1);

                try {
                    TextureUtil.uploadTextureMipmap(
                        textureatlassprite1.getFrameTextureData(0),
                        textureatlassprite1.getIconWidth(),
                        textureatlassprite1.getIconHeight(),
                        textureatlassprite1.getOriginX(),
                        textureatlassprite1.getOriginY(),
                        false,
                        false);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                    CrashReportCategory crashreportcategory = crashreport
                        .makeCategory("Texture being stitched together");
                    crashreportcategory.addCrashSection("Atlas path", this.basePath);
                    crashreportcategory.addCrashSection("Sprite", textureatlassprite1);
                    throw new ReportedException(crashreport);
                }

                if (textureatlassprite1.hasAnimationMetadata()) {
                    this.listAnimatedSprites.add(textureatlassprite1);
                }
            }
        }

        for (TextureAtlasSprite textureatlassprite2 : map.values()) {
            textureatlassprite2.copyFrom(this.missingImage);
        }
        net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
        net.minecraftforge.fml.common.ProgressManager.pop(bar);
    }

    private boolean generateMipmaps(IResourceManager resourceManager, final TextureAtlasSprite texture) {
        ResourceLocation resourcelocation = this.getResourceLocation(texture);
        IResource iresource = null;
        label62:
        {
            boolean flag;
            if (texture.hasCustomLoader(resourceManager, resourcelocation)) break label62;
            try {
                iresource = resourceManager.getResource(resourcelocation);
                texture.loadSpriteFrames(iresource, this.mipmapLevels + 1);
                break label62;
            } catch (RuntimeException runtimeexception) {
                LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
                flag = false;
            } catch (IOException ioexception) {
                LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
                flag = false;
                return flag;
            } finally {
                IOUtils.closeQuietly((Closeable) iresource);
            }

            return flag;
        }

        try {
            texture.generateMipmaps(this.mipmapLevels);
            return true;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
            crashreportcategory.addDetail("Sprite name", new ICrashReportDetail<String>() {

                public String call() throws Exception {
                    return texture.getIconName();
                }
            });
            crashreportcategory.addDetail("Sprite size", new ICrashReportDetail<String>() {

                public String call() throws Exception {
                    return texture.getIconWidth() + " x " + texture.getIconHeight();
                }
            });
            crashreportcategory.addDetail("Sprite frames", new ICrashReportDetail<String>() {

                public String call() throws Exception {
                    return texture.getFrameCount() + " frames";
                }
            });
            crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
            throw new ReportedException(crashreport);
        }
    }

    private ResourceLocation getResourceLocation(TextureAtlasSprite p_184396_1_) {
        ResourceLocation resourcelocation = new ResourceLocation(p_184396_1_.getIconName());
        return new ResourceLocation(
            resourcelocation.getResourceDomain(),
            String.format("%s/%s%s", this.basePath, resourcelocation.getResourcePath(), ".png"));
    }

    public TextureAtlasSprite getAtlasSprite(String iconName) {
        TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);

        if (textureatlassprite == null) {
            textureatlassprite = this.missingImage;
        }

        return textureatlassprite;
    }

    public void updateAnimations() {
        TextureUtil.bindTexture(this.getGlTextureId());

        for (TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
            textureatlassprite.updateAnimation();
        }
    }

    public TextureAtlasSprite registerSprite(ResourceLocation location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null!");
        } else {
            TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(location.toString());

            if (textureatlassprite == null) {
                textureatlassprite = TextureAtlasSprite.makeAtlasSprite(location);
                this.mapRegisteredSprites.put(location.toString(), textureatlassprite);
            }

            return textureatlassprite;
        }
    }

    public void tick() {
        this.updateAnimations();
    }

    public void setMipmapLevels(int mipmapLevelsIn) {
        this.mipmapLevels = mipmapLevelsIn;
    }

    public TextureAtlasSprite getMissingSprite() {
        return this.missingImage;
    }

    // ===================================================================================================
    // Forge Start
    // ===================================================================================================

    private final java.util.Deque<ResourceLocation> loadingSprites = new java.util.ArrayDeque<>();
    private final java.util.Set<ResourceLocation> loadedSprites = new java.util.HashSet<>();

    /**
     * Grabs the registered entry for the specified name, returning null if there was not a entry.
     * Opposed to registerIcon, this will not instantiate the entry, useful to test if a mapping exists.
     *
     * @param name The name of the entry to find
     * @return The registered entry, null if nothing was registered.
     */
    @Nullable
    public TextureAtlasSprite getTextureExtry(String name) {
        return mapRegisteredSprites.get(name);
    }

    /**
     * Adds a texture registry entry to this map for the specified name if one does not already exist.
     * Returns false if the map already contains a entry for the specified name.
     *
     * @param entry Entry instance
     * @return True if the entry was added to the map, false otherwise.
     */
    public boolean setTextureEntry(TextureAtlasSprite entry) {
        String name = entry.getIconName();
        if (!mapRegisteredSprites.containsKey(name)) {
            mapRegisteredSprites.put(name, entry);
            return true;
        }
        return false;
    }

    public String getBasePath() {
        return basePath;
    }

    public int getMipmapLevels() {
        return mipmapLevels;
    }
}
