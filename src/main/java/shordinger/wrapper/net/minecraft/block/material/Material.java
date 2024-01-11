package shordinger.wrapper.net.minecraft.block.material;

import static shordinger.wrapper.net.minecraft.block.material.EnumPushReaction.*;

public class Material extends net.minecraft.block.material.Material {

    public static final Material AIR = (Material) net.minecraft.block.material.Material.air;
    public static final Material GRASS = (Material) net.minecraft.block.material.Material.grass;
    public static final Material GROUND = (Material) net.minecraft.block.material.Material.ground;
    public static final Material WOOD = (Material) net.minecraft.block.material.Material.wood;
    public static final Material ROCK = (Material) net.minecraft.block.material.Material.rock;
    public static final Material IRON = (Material) net.minecraft.block.material.Material.iron;
    public static final Material ANVIL = (Material) net.minecraft.block.material.Material.anvil;
    public static final Material WATER = (Material) net.minecraft.block.material.Material.water;
    public static final Material LAVA = (Material) net.minecraft.block.material.Material.lava;
    public static final Material LEAVES = (Material) net.minecraft.block.material.Material.leaves;
    public static final Material PLANTS = (Material) net.minecraft.block.material.Material.plants;
    public static final Material VINE = (Material) net.minecraft.block.material.Material.vine;
    public static final Material SPONGE = (Material) net.minecraft.block.material.Material.sponge;
    public static final Material CLOTH = (Material) net.minecraft.block.material.Material.cloth;
    public static final Material FIRE = (Material) net.minecraft.block.material.Material.fire;
    public static final Material SAND = (Material) net.minecraft.block.material.Material.sand;
    public static final Material CIRCUITS = (Material) net.minecraft.block.material.Material.circuits;
    public static final Material CARPET = (Material) net.minecraft.block.material.Material.carpet;
    public static final Material GLASS = (Material) net.minecraft.block.material.Material.glass;
    public static final Material REDSTONE_LIGHT = (Material) net.minecraft.block.material.Material.redstoneLight;
    public static final Material TNT = (Material) net.minecraft.block.material.Material.tnt;
    public static final Material CORAL = (Material) net.minecraft.block.material.Material.coral;
    public static final Material ICE = (Material) net.minecraft.block.material.Material.ice;
    public static final Material PACKED_ICE = (Material) net.minecraft.block.material.Material.packedIce;
    public static final Material SNOW = (Material) net.minecraft.block.material.Material.snow;
    public static final Material CRAFTED_SNOW = (Material) net.minecraft.block.material.Material.craftedSnow;
    public static final Material CACTUS = (Material) net.minecraft.block.material.Material.cactus;
    public static final Material CLAY = (Material) net.minecraft.block.material.Material.clay;
    public static final Material GOURD = (Material) net.minecraft.block.material.Material.gourd;
    public static final Material DRAGON_EGG = (Material) net.minecraft.block.material.Material.dragonEgg;
    public static final Material PORTAL = (Material) net.minecraft.block.material.Material.portal;
    public static final Material CAKE = (Material) net.minecraft.block.material.Material.cake;
    public static final Material WEB = (Material) net.minecraft.block.material.Material.web;
    public static final Material PISTON = (new Material(MapColor.STONE)).setImmovableMobility();
    public static final Material BARRIER = (new Material(MapColor.AIR)).setRequiresTool()
        .setImmovableMobility();
    public static final Material STRUCTURE_VOID = new MaterialTransparent(
        net.minecraft.block.material.MapColor.airColor);
    // /**
    // * Bool defining if the block can burn or not.
    // */
    // private boolean canBurn;
    // /**
    // * Determines whether blocks with this material can be "overwritten" by other blocks when placed - eg snow, vines
    // * and tall grass.
    // */
    // private boolean replaceable;
    // /**
    // * Indicates if the material is translucent
    // */
    // private boolean isTranslucent;
    // /**
    // * The color index used to draw the blocks of this material on maps.
    // */
    // private final MapColor materialMapColor;
    // /**
    // * Determines if the material can be harvested without a tool (or with the wrong tool)
    // */
    // private boolean requiresNoTool = true;
    // /**
    // * Mobility information flag. 0 indicates that this block is normal, 1 indicates that it can't push other blocks,
    // 2
    // * indicates that it can't be pushed.
    // */
    // private EnumPushReaction mobilityFlag = EnumPushReaction.NORMAL;
    // private boolean isAdventureModeExempt;
    //
    // public net.minecraft.block.material.Material oldData;

    public Material(net.minecraft.block.material.MapColor color) {
        super(color);
    }

    public Material setRequiresTool() {
        return (Material) super.setRequiresTool();
    }

    /**
     * Returns true if the block is a considered solid. This is true by default.
     */
    public boolean isSolid() {
        return true;
    }

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
     */
    public boolean blocksLight() {
        return true;
    }

    /**
     * Returns if the block can burn or not.
     */
    public boolean getCanBurn() {
        return super.getCanBurn();
    }

    public Material setReplaceable() {
        return (Material) super.setReplaceable();
    }

    public EnumPushReaction getMobilityFlag() {
        int flag = getMaterialMobility();
        switch (flag) {
            case 0 -> {
                return NORMAL;
            }
            case 1 -> {
                return DESTROY;
            }
            case 2 -> {
                return BLOCK;
            }
            case 3 -> {
                return IGNORE;
            }
            case 4 -> {
                return PUSH_ONLY;
            }
        }
        return NORMAL;
    }

    /**
     * This type of material can't be pushed, but pistons can move over it.
     */
    protected Material setNoPushMobility() {
        return (Material) super.setNoPushMobility();
    }

    /**
     * This type of material can't be pushed, and pistons are blocked to move.
     */
    public Material setImmovableMobility() {
        return (Material) super.setImmovableMobility();
    }

    protected Material setAdventureModeExempt() {
        return (Material) super.setAdventureModeExempt();
    }

    /**
     * Retrieves the color index of the block. This is is the same color used by vanilla maps to represent this block.
     */
    public net.minecraft.block.material.MapColor getMaterialMapColor() {
        return super.getMaterialMapColor();
    }
}
