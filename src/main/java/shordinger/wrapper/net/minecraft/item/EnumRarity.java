package shordinger.wrapper.net.minecraft.item;

import shordinger.wrapper.net.minecraft.util.text.TextFormatting;
import shordinger.wrapper.net.minecraftforge.common.IRarity;

public enum EnumRarity implements IRarity {

    COMMON(TextFormatting.WHITE, "Common"),
    UNCOMMON(TextFormatting.YELLOW, "Uncommon"),
    RARE(TextFormatting.AQUA, "Rare"),
    EPIC(TextFormatting.LIGHT_PURPLE, "Epic");

    /**
     * The color assigned to this rarity type.
     */
    public final TextFormatting rarityColor;
    /**
     * Rarity name.
     */
    public final String rarityName;

    private EnumRarity(TextFormatting color, String name) {
        this.rarityColor = color;
        this.rarityName = name;
    }

    @Override
    public TextFormatting getColor() {
        return this.rarityColor;
    }

    @Override
    public String getName() {
        return this.rarityName;
    }
}
