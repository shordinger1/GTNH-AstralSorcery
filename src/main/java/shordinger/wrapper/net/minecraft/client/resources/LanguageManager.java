package shordinger.wrapper.net.minecraft.client.resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.resources.data.LanguageMetadataSection;
import shordinger.wrapper.net.minecraft.client.resources.data.MetadataSerializer;
import shordinger.wrapper.net.minecraft.util.text.translation.LanguageMap;

@SideOnly(Side.CLIENT)
public class LanguageManager implements IResourceManagerReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MetadataSerializer metadataSerializer;
    private String currentLanguage;
    protected static final Locale CURRENT_LOCALE = new Locale();
    private final Map<String, Language> languageMap = Maps.<String, Language>newHashMap();

    public LanguageManager(MetadataSerializer theMetadataSerializerIn, String currentLanguageIn) {
        this.metadataSerializer = theMetadataSerializerIn;
        this.currentLanguage = currentLanguageIn;
        I18n.setLocale(CURRENT_LOCALE);
    }

    public void parseLanguageMetadata(List<IResourcePack> resourcesPacks) {
        this.languageMap.clear();

        for (IResourcePack iresourcepack : resourcesPacks) {
            try {
                LanguageMetadataSection languagemetadatasection = (LanguageMetadataSection) iresourcepack
                    .getPackMetadata(this.metadataSerializer, "language");

                if (languagemetadatasection != null) {
                    for (Language language : languagemetadatasection.getLanguages()) {
                        if (!this.languageMap.containsKey(language.getLanguageCode())) {
                            this.languageMap.put(language.getLanguageCode(), language);
                        }
                    }
                }
            } catch (RuntimeException runtimeexception) {
                LOGGER.warn(
                    "Unable to parse language metadata section of resourcepack: {}",
                    iresourcepack.getPackName(),
                    runtimeexception);
            } catch (IOException ioexception) {
                LOGGER.warn(
                    "Unable to parse language metadata section of resourcepack: {}",
                    iresourcepack.getPackName(),
                    ioexception);
            }
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        List<String> list = Lists.newArrayList("en_us");

        if (!"en_us".equals(this.currentLanguage)) {
            list.add(this.currentLanguage);
        }

        CURRENT_LOCALE.loadLocaleDataFiles(resourceManager, list);
        LanguageMap.replaceWith(CURRENT_LOCALE.properties);
    }

    public boolean isCurrentLocaleUnicode() {
        return CURRENT_LOCALE.isUnicode();
    }

    public boolean isCurrentLanguageBidirectional() {
        return this.getCurrentLanguage() != null && this.getCurrentLanguage()
            .isBidirectional();
    }

    public void setCurrentLanguage(Language currentLanguageIn) {
        this.currentLanguage = currentLanguageIn.getLanguageCode();
    }

    public Language getCurrentLanguage() {
        String s = this.languageMap.containsKey(this.currentLanguage) ? this.currentLanguage : "en_us";
        return this.languageMap.get(s);
    }

    public SortedSet<Language> getLanguages() {
        return Sets.newTreeSet(this.languageMap.values());
    }

    public Language getLanguage(String p_191960_1_) {
        return this.languageMap.get(p_191960_1_);
    }
}
