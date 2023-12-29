//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shordinger.astralsorcery.migration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public enum TextFormatting {

    BLACK("BLACK", '0', 0),
    DARK_BLUE("DARK_BLUE", '1', 1),
    DARK_GREEN("DARK_GREEN", '2', 2),
    DARK_AQUA("DARK_AQUA", '3', 3),
    DARK_RED("DARK_RED", '4', 4),
    DARK_PURPLE("DARK_PURPLE", '5', 5),
    GOLD("GOLD", '6', 6),
    GRAY("GRAY", '7', 7),
    DARK_GRAY("DARK_GRAY", '8', 8),
    BLUE("BLUE", '9', 9),
    GREEN("GREEN", 'a', 10),
    AQUA("AQUA", 'b', 11),
    RED("RED", 'c', 12),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
    YELLOW("YELLOW", 'e', 14),
    WHITE("WHITE", 'f', 15),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1);

    private static final Map<String, TextFormatting> NAME_MAPPING = Maps.newHashMap();
    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    private final String name;
    private final boolean fancyStyling;
    private final String controlString;
    private final int colorIndex;

    private static String lowercaseAlpha(String p_175745_0_) {
        return p_175745_0_.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z]", "");
    }

    private TextFormatting(String formattingName, char formattingCodeIn, int colorIndex) {
        this(formattingName, formattingCodeIn, false, colorIndex);
    }

    private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn) {
        this(formattingName, formattingCodeIn, fancyStylingIn, -1);
    }

    private TextFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn, int colorIndex) {
        this.name = formattingName;
        this.fancyStyling = fancyStylingIn;
        this.colorIndex = colorIndex;
        this.controlString = "§" + formattingCodeIn;
    }

    public int getColorIndex() {
        return this.colorIndex;
    }

    public boolean isFancyStyling() {
        return this.fancyStyling;
    }

    public boolean isColor() {
        return !this.fancyStyling && this != RESET;
    }

    public String getFriendlyName() {
        return this.name()
            .toLowerCase(Locale.ROOT);
    }

    public String toString() {
        return this.controlString;
    }

    @Nullable
    public static String getTextWithoutFormattingCodes(@Nullable String text) {
        return text == null ? null
            : FORMATTING_CODE_PATTERN.matcher(text)
            .replaceAll("");
    }

    @Nullable
    public static TextFormatting getValueByName(@Nullable String friendlyName) {
        return friendlyName == null ? null : (TextFormatting) NAME_MAPPING.get(lowercaseAlpha(friendlyName));
    }

    @Nullable
    public static TextFormatting fromColorIndex(int index) {
        if (index < 0) {
            return RESET;
        } else {
            TextFormatting[] var1 = values();
            int var2 = var1.length;

            for (TextFormatting textformatting : var1) {
                if (textformatting.getColorIndex() == index) {
                    return textformatting;
                }
            }

            return null;
        }
    }

    public static Collection<String> getValidValues(boolean p_96296_0_, boolean p_96296_1_) {
        List<String> list = Lists.newArrayList();
        TextFormatting[] var3 = values();
        int var4 = var3.length;

        for (TextFormatting textformatting : var3) {
            if ((!textformatting.isColor() || p_96296_0_) && (!textformatting.isFancyStyling() || p_96296_1_)) {
                list.add(textformatting.getFriendlyName());
            }
        }

        return list;
    }

    static {
        TextFormatting[] var0 = values();
        int var1 = var0.length;

        for (TextFormatting textformatting : var0) {
            NAME_MAPPING.put(lowercaseAlpha(textformatting.name), textformatting);
        }

    }
}

public abstract class TextComponentBase implements IChatComponent {

    protected List<IChatComponent> siblings = Lists.newArrayList();
    private ChatStyle ChatStyle;

    public TextComponentBase() {
    }

    public IChatComponent appendSibling(IChatComponent component) {
        component.getChatStyle()
            .setParentStyle(this.getChatStyle());
        this.siblings.add(component);
        return this;
    }

    public List<IChatComponent> getSiblings() {
        return this.siblings;
    }

    public IChatComponent appendText(String text) {
        return this.appendSibling(new TextComponentString(text));
    }

    public IChatComponent setChatStyle(ChatStyle ChatStyle) {
        this.ChatStyle = ChatStyle;

        for (IChatComponent itextcomponent : this.siblings) {
            itextcomponent.getChatStyle()
                .setParentStyle(this.getChatStyle());
        }

        return this;
    }

    public ChatStyle getChatStyle() {
        if (this.ChatStyle == null) {
            this.ChatStyle = new ChatStyle();

            for (IChatComponent itextcomponent : this.siblings) {
                itextcomponent.getChatStyle()
                    .setParentStyle(this.ChatStyle);
            }
        }

        return this.ChatStyle;
    }

    public Iterator<IChatComponent> iterator() {
        return Iterators
            .concat(Iterators.forArray(this), createDeepCopyIterator(this.siblings));
    }

    public final String getUnformattedText() {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent iChatComponent : (Iterable<IChatComponent>) this) {
            stringbuilder.append(((IChatComponent) iChatComponent).getUnformattedComponentText());
        }

        return stringbuilder.toString();
    }

    public final String getFormattedText() {
        StringBuilder stringbuilder = new StringBuilder();

        for (IChatComponent iChatComponent : (Iterable<IChatComponent>) this) {
            String s = ((IChatComponent) iChatComponent).getUnformattedComponentText();
            if (s.stackSize!=0) {
                stringbuilder.append(
                    ((IChatComponent) iChatComponent).getChatStyle()
                        .getFormattingCode());
                stringbuilder.append(s);
                stringbuilder.append(TextFormatting.RESET);
            }
        }

        return stringbuilder.toString();
    }

    public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components) {
        Iterator<IChatComponent> iterator = Iterators.concat(
            Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {

                public Iterator<IChatComponent> apply(@Nullable IChatComponent p_apply_1_) {
                    return p_apply_1_.iterator();
                }
            }));
        iterator = Iterators.transform(iterator, new Function<IChatComponent, IChatComponent>() {

            public IChatComponent apply(@Nullable IChatComponent p_apply_1_) {
                IChatComponent itextcomponent = p_apply_1_.createCopy();
                itextcomponent.setChatStyle(
                    itextcomponent.getChatStyle()
                        .createDeepCopy());
                return itextcomponent;
            }
        });
        return iterator;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof TextComponentBase)) {
            return false;
        } else {
            TextComponentBase textcomponentbase = (TextComponentBase) p_equals_1_;
            return this.siblings.equals(textcomponentbase.siblings) && this.getChatStyle()
                .equals(textcomponentbase.getChatStyle());
        }
    }

    public int hashCode() {
        return 31 * this.ChatStyle.hashCode() + this.siblings.hashCode();
    }

    public String toString() {
        return "BaseComponent{ChatStyle=" + this.ChatStyle + ", siblings=" + this.siblings + '}';
    }
}

