package shordinger.wrapper.net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class I18n {

    public static Locale i18nLocale;

    static void setLocale(Locale i18nLocaleIn) {
        i18nLocale = i18nLocaleIn;
    }

    /**
     * Translates the given string and then formats it. Equivilant to String.format(translate(key), parameters).
     */
    public static String format(String translateKey, Object... parameters) {
        return i18nLocale.formatMessage(translateKey, parameters);
    }

    public static boolean hasKey(String key) {
        return i18nLocale.hasKey(key);
    }
}
