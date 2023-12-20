package com.astralsorcery.gtnh_astralsorcery.lib;

import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.astralsorcery.gtnh_astralsorcery.AstralSorcery;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * When Texts need auto generate .lang . Use this.
 */
public class TextHandler {

    /* The Map across all text<Key, Value> */
    public static Map<String, String> LangMap;
    // public static Map<String, String> LangMapBackUp;
    public static Map<String, String> LangMapNeedToWrite = new HashMap<>();

    public static String texter(String key) {
        return translateToLocalFormatted(key);
    }

    /**
     * Init LangMap.
     *
     * @param isInDevMode The signal of whether in development mode.
     */
    public static void initLangMap(Boolean isInDevMode) {
        if (isInDevMode) {
            /* Parse the .lang in LangMap */
            LangMap = LanguageUtil0.parseLangFile("en_US");
            // LangMapBackUp = new HashMap<String, String>(LangMap);
        }

    }

    /**
     * Write the new textLines into Dev/src/main/resources/gtnhcommunitymod/lang/.lang
     *
     * @param isInDevMode The signal of whether in development mode.
     */
    public static void serializeLangMap(boolean isInDevMode) {

        if (isInDevMode) {

            /* If no new text need to write */
            if (LangMapNeedToWrite.isEmpty()) {
                return;
            }

            /* Prepare the files. */
            File en_US_lang = new File("\\assets\\gtnhcommunitymod\\lang\\en_US.lang");
            File zh_CN_lang = new File("\\assets\\gtnhcommunitymod\\lang\\zh_CN.lang");
            AstralSorcery.LOG
                .info("File finder with en_US.lang catch a file absolutePath: " + en_US_lang.getAbsolutePath());
            AstralSorcery.LOG.info("File finder with en_US.lang catch a file named: " + en_US_lang.getName());

            /* Write the new textLines in the end of the lang file. */
            AstralSorcery.LOG.info("Start write new text: " + en_US_lang.getAbsolutePath());

            try {
                FileWriter en_Us = new FileWriter(en_US_lang, true);
                FileWriter zh_CN = new FileWriter(zh_CN_lang, true);
                for (String key : LangMapNeedToWrite.keySet()) {
                    AstralSorcery.LOG.info("en_US write a Line START: " + key + "===>" + LangMapNeedToWrite.get(key));
                    en_Us.write(key);
                    en_Us.write("=");
                    en_Us.write(LangMapNeedToWrite.get(key));
                    en_Us.write("\n");
                    AstralSorcery.LOG.info("en_US write a Line COMPLETE.");
                    AstralSorcery.LOG.info("zh_CN write a Line START: " + key + "===>" + LangMapNeedToWrite.get(key));
                    zh_CN.write(key);
                    zh_CN.write("=");
                    zh_CN.write(LangMapNeedToWrite.get(key));
                    zh_CN.write("\n");
                    AstralSorcery.LOG.info("zh_CN write a Line COMPLETE.");
                }
                AstralSorcery.LOG.info("Finish to write new text: " + en_US_lang.getAbsolutePath());
                en_Us.close();
                zh_CN.close();
            } catch (IOException e) {
                AstralSorcery.LOG.info("Error in serializeLangMap() File Writer en_US");
                throw new RuntimeException(e);
            }

            /* Del the backup. */
            LangMapNeedToWrite.clear();

        }
    }
}

class LanguageUtil0 {

    private static Map<String, String> zhCN;
    private static Map<String, String> currentLang;
    private static List<String> itemNameKey;
    private static List<String> blockNameKey;
    private static Map<String, String> nameKey2DescriptionKey;

    static {
        init();
        System.out.println("abcAFCR:LanguageUtil0 init completed");
    }

    public static Map<String, String> getZhCN() {
        return zhCN;
    }

    public static Map<String, String> getCurrentLang() {
        return currentLang;
    }

    public static List<String> getItemNameKey() {
        return itemNameKey;
    }

    public static List<String> getBlockNameKey() {
        return blockNameKey;
    }

    public static String getCurLangDescription(String nameKey) {
        String descriptionKey = nameKey2DescriptionKey.get(nameKey);
        String description = currentLang.get(descriptionKey);
        return description == null ? zhCN.get(descriptionKey) : description;
    }

    public static String getCurLangItemName(String nameKey) {
        String name = currentLang.get(nameKey);
        return name == null ? zhCN.get(nameKey) : name;
    }

    private static Map<String, String> parseLangFile() {
        return parseLangFile("zh_CN");
    }

    public static Map<String, String> parseLangFile(String currentLangCode) {
        return parseLangFile("/assets/gtnhcommunitymod/lang/", currentLangCode);
    }

    public static Map<String, String> parseLangFile(String langPath, String currentLangCode) {
        String fullLangPath = langPath + currentLangCode + ".lang";
        List<String> langList = getLangList(fullLangPath);
        if (langList == null) return null;

        Map<String, String> map = new HashMap<String, String>();
        Splitter equalSignSplitter = Splitter.on('=')
            .limit(2);

        for (String s : langList) {
            // s的5种情况，前缀#的注释，\n的空字符串""，正常键值对[item.gold.name=gold],错误键值对[a=][=a],任意字符串[oojoew]
            // 剔除[前缀#的注释]与[\n的空字符串""]两种字符串类型
            if (!s.isEmpty() && s.charAt(0) != 35) {
                String[] sArr = Iterables.toArray(equalSignSplitter.split(s), String.class);

                // 剔除[错误键值对[a=][=a]]和[任意字符串[oojoew]]两种字符串类型
                if (sArr != null && sArr.length == 2 && !"".equals(sArr[0]) && !"".equals(sArr[1])) {
                    map.put(sArr[0], sArr[1]);
                }
            }
        }

        return map;
    }

    private static List<String> getLangList(String fullLangPath) {
        InputStream langIS = LanguageUtil0.class.getResourceAsStream(fullLangPath);
        if (langIS == null) return null;

        List<String> list = null;
        try {
            list = IOUtils.readLines(langIS, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                langIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private static void init() {
        zhCN = parseLangFile();
        String currentLangCode = Minecraft.getMinecraft()
            .getLanguageManager()
            .getCurrentLanguage()
            .getLanguageCode();
        currentLang = parseLangFile(currentLangCode);
        itemNameKey = new ArrayList<String>();
        blockNameKey = new ArrayList<String>();
        nameKey2DescriptionKey = new HashMap<String, String>();

        for (String s : zhCN.keySet()) {

            if (s.startsWith("item.")) {
                itemNameKey.add(s);
            } else if (s.startsWith("tile.")) {
                blockNameKey.add(s);
            } else if (s.startsWith("noteBook.") && s.endsWith(".description")) {
                String name = s.substring(s.indexOf(".") + 1, s.lastIndexOf(".")) + ".name";
                nameKey2DescriptionKey.put(name, s);
            }

        }
    }

}
