package shordinger.wrapper.net.minecraft.creativetab;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.enchantment.EnumEnchantmentType;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.NonNullList;

public abstract class CreativeTabs extends net.minecraft.creativetab.CreativeTabs {

    public static CreativeTabs[] CREATIVE_TAB_ARRAY = (CreativeTabs[]) creativeTabArray;
    public static final CreativeTabs BUILDING_BLOCKS = (CreativeTabs) tabBlock;
    public static final CreativeTabs DECORATIONS = (CreativeTabs) tabDecorations;
    public static final CreativeTabs REDSTONE = (CreativeTabs) tabRedstone;
    public static final CreativeTabs TRANSPORTATION = (CreativeTabs) tabTransport;
    public static final CreativeTabs MISC = (CreativeTabs) tabMisc;
    public static final CreativeTabs SEARCH = (CreativeTabs) tabAllSearch;
    public static final CreativeTabs FOOD = (CreativeTabs) tabFood;
    public static final CreativeTabs TOOLS = (CreativeTabs) tabTools;
    public static final CreativeTabs COMBAT = (CreativeTabs) tabCombat;
    public static final CreativeTabs BREWING = (CreativeTabs) tabBrewing;
    public static final CreativeTabs MATERIALS = (CreativeTabs) tabMaterials;
    public static final CreativeTabs HOTBAR = new CreativeTabs(4, "hotbar") {

        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(Blocks.BOOKSHELF);
        }

        /**
         * only shows items which have tabToDisplayOn == this
         */
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
            throw new RuntimeException("Implement exception client-side.");
        }

        @SideOnly(Side.CLIENT)
        public boolean isAlignedRight() {
            return true;
        }
    };
    public static final CreativeTabs INVENTORY = (CreativeTabs) tabInventory;
    private String backgroundTexture = "items.png";
    private EnumEnchantmentType[] enchantmentTypes = new EnumEnchantmentType[0];
    private ItemStack iconItemStack;

    public CreativeTabs(String label) {
        this(getNextID(), label);
    }

    public CreativeTabs(int index, String label) {
        super(index, label);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack() {
        if (this.iconItemStack.isEmpty()) {
            this.iconItemStack = this.getTabIconItem();
        }

        return this.iconItemStack;
    }

    @SideOnly(Side.CLIENT)
    public abstract ItemStack getTabIconItem();

    @SideOnly(Side.CLIENT)
    public String getBackgroundImageName() {
        return this.backgroundTexture;
    }

    @SideOnly(Side.CLIENT)
    public boolean isAlignedRight() {
        return this.getTabColumn() == 5;
    }

    /**
     * Returns the enchantment types relevant to this tab
     */
    public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
        return this.enchantmentTypes;
    }

    /**
     * Sets the enchantment types for populating this tab with enchanting books
     */
    public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... types) {
        this.enchantmentTypes = types;
        return this;
    }

    public boolean hasRelevantEnchantmentType(@Nullable EnumEnchantmentType enchantmentType) {
        if (enchantmentType != null) {
            for (EnumEnchantmentType enumenchantmenttype : this.enchantmentTypes) {
                if (enumenchantmenttype == enchantmentType) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * only shows items which have tabToDisplayOn == this
     */
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
        for (Item item : Item.REGISTRY) {
            item.getSubItems(this, p_78018_1_);
        }
    }

    @SideOnly(Side.CLIENT)
    public net.minecraft.util.ResourceLocation getBackgroundImage() {
        return new net.minecraft.util.ResourceLocation(
            "textures/gui/container/creative_inventory/tab_" + this.getBackgroundImageName());
    }

    public int getLabelColor() {
        return 4210752;
    }
}
