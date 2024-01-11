package shordinger.wrapper.net.minecraft.client.resources.data;

import java.util.Collection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.resources.Language;

@SideOnly(Side.CLIENT)
public class LanguageMetadataSection implements IMetadataSection {

    private final Collection<Language> languages;

    public LanguageMetadataSection(Collection<Language> languagesIn) {
        this.languages = languagesIn;
    }

    public Collection<Language> getLanguages() {
        return this.languages;
    }
}
