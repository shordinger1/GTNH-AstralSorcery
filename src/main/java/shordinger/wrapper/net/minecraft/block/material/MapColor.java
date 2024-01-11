package shordinger.wrapper.net.minecraft.block.material;

import shordinger.wrapper.net.minecraft.item.EnumDyeColor;

public class MapColor {

    /**
     * Holds all the 16 colors used on maps, very similar of a pallete system.
     */
    public static final net.minecraft.block.material.MapColor[] BLOCK_COLORS = new net.minecraft.block.material.MapColor[16];
    public static final net.minecraft.block.material.MapColor AIR = net.minecraft.block.material.MapColor.airColor;
    public static final net.minecraft.block.material.MapColor GRASS = net.minecraft.block.material.MapColor.grassColor;
    public static final net.minecraft.block.material.MapColor SAND = net.minecraft.block.material.MapColor.sandColor;
    public static final net.minecraft.block.material.MapColor CLOTH = net.minecraft.block.material.MapColor.clothColor;
    public static final net.minecraft.block.material.MapColor TNT = net.minecraft.block.material.MapColor.tntColor;
    public static final net.minecraft.block.material.MapColor ICE = net.minecraft.block.material.MapColor.iceColor;
    public static final net.minecraft.block.material.MapColor IRON = net.minecraft.block.material.MapColor.ironColor;
    public static final net.minecraft.block.material.MapColor FOLIA = net.minecraft.block.material.MapColor.foliageColor;
    public static final net.minecraft.block.material.MapColor SNOW = net.minecraft.block.material.MapColor.snowColor;
    public static final net.minecraft.block.material.MapColor CLAY = net.minecraft.block.material.MapColor.clayColor;
    public static final net.minecraft.block.material.MapColor DIRT = net.minecraft.block.material.MapColor.dirtColor;
    public static final net.minecraft.block.material.MapColor STONE = net.minecraft.block.material.MapColor.stoneColor;
    public static final net.minecraft.block.material.MapColor WATER = net.minecraft.block.material.MapColor.waterColor;
    public static final net.minecraft.block.material.MapColor WOOD = net.minecraft.block.material.MapColor.woodColor;
    public static final net.minecraft.block.material.MapColor QUARTZ = net.minecraft.block.material.MapColor.quartzColor;
    public static final net.minecraft.block.material.MapColor ADOBE = net.minecraft.block.material.MapColor.adobeColor;
    public static final net.minecraft.block.material.MapColor MAGENTA = net.minecraft.block.material.MapColor.magentaColor;
    public static final net.minecraft.block.material.MapColor LIGHT_BLUE = net.minecraft.block.material.MapColor.lightBlueColor;
    public static final net.minecraft.block.material.MapColor YELLOW = net.minecraft.block.material.MapColor.yellowColor;
    public static final net.minecraft.block.material.MapColor LIME = net.minecraft.block.material.MapColor.limeColor;
    public static final net.minecraft.block.material.MapColor PINK = net.minecraft.block.material.MapColor.pinkColor;
    public static final net.minecraft.block.material.MapColor GRAY = net.minecraft.block.material.MapColor.grayColor;
    public static final net.minecraft.block.material.MapColor SILVER = net.minecraft.block.material.MapColor.silverColor;
    public static final net.minecraft.block.material.MapColor CYAN = net.minecraft.block.material.MapColor.cyanColor;
    public static final net.minecraft.block.material.MapColor PURPLE = net.minecraft.block.material.MapColor.purpleColor;
    public static final net.minecraft.block.material.MapColor BLUE = net.minecraft.block.material.MapColor.blueColor;
    public static final net.minecraft.block.material.MapColor BROWN = net.minecraft.block.material.MapColor.brownColor;
    public static final net.minecraft.block.material.MapColor GREEN = net.minecraft.block.material.MapColor.greenColor;
    public static final net.minecraft.block.material.MapColor RED = net.minecraft.block.material.MapColor.redColor;
    public static final net.minecraft.block.material.MapColor BLACK = net.minecraft.block.material.MapColor.blackColor;
    public static final net.minecraft.block.material.MapColor GOLD = net.minecraft.block.material.MapColor.goldColor;
    public static final net.minecraft.block.material.MapColor DIAMOND = net.minecraft.block.material.MapColor.diamondColor;
    public static final net.minecraft.block.material.MapColor LAPIS = net.minecraft.block.material.MapColor.lapisColor;
    public static final net.minecraft.block.material.MapColor EMERALD = net.minecraft.block.material.MapColor.emeraldColor;
    public static final net.minecraft.block.material.MapColor OBSIDIAN = net.minecraft.block.material.MapColor.obsidianColor;
    public static final net.minecraft.block.material.MapColor NETHERRACK = net.minecraft.block.material.MapColor.netherrackColor;
    public static final net.minecraft.block.material.MapColor WHITE_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor ORANGE_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor MAGENTA_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor LIGHT_BLUE_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor YELLOW_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor LIME_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor PINK_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor GRAY_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor SILVER_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor CYAN_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor PURPLE_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor BLUE_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor BROWN_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor GREEN_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor RED_STAINED_HARDENED_CLAY = AIR;
    public static final net.minecraft.block.material.MapColor BLACK_STAINED_HARDENED_CLAY = AIR;

    public static net.minecraft.block.material.MapColor getBlockColor(EnumDyeColor dyeColorIn) {
        return BLOCK_COLORS[dyeColorIn.getMetadata()];
    }

    static {
        BLOCK_COLORS[EnumDyeColor.WHITE.getMetadata()] = SNOW;
        BLOCK_COLORS[EnumDyeColor.ORANGE.getMetadata()] = ADOBE;
        BLOCK_COLORS[EnumDyeColor.MAGENTA.getMetadata()] = MAGENTA;
        BLOCK_COLORS[EnumDyeColor.LIGHT_BLUE.getMetadata()] = LIGHT_BLUE;
        BLOCK_COLORS[EnumDyeColor.YELLOW.getMetadata()] = YELLOW;
        BLOCK_COLORS[EnumDyeColor.LIME.getMetadata()] = LIME;
        BLOCK_COLORS[EnumDyeColor.PINK.getMetadata()] = PINK;
        BLOCK_COLORS[EnumDyeColor.GRAY.getMetadata()] = GRAY;
        BLOCK_COLORS[EnumDyeColor.SILVER.getMetadata()] = SILVER;
        BLOCK_COLORS[EnumDyeColor.CYAN.getMetadata()] = CYAN;
        BLOCK_COLORS[EnumDyeColor.PURPLE.getMetadata()] = PURPLE;
        BLOCK_COLORS[EnumDyeColor.BLUE.getMetadata()] = BLUE;
        BLOCK_COLORS[EnumDyeColor.BROWN.getMetadata()] = BROWN;
        BLOCK_COLORS[EnumDyeColor.GREEN.getMetadata()] = GREEN;
        BLOCK_COLORS[EnumDyeColor.RED.getMetadata()] = RED;
        BLOCK_COLORS[EnumDyeColor.BLACK.getMetadata()] = BLACK;
    }
}
