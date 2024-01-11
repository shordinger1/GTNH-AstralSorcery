package shordinger.wrapper.net.minecraft.enchantment;

import shordinger.wrapper.net.minecraft.block.BlockPumpkin;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemArmor;
import shordinger.wrapper.net.minecraft.item.ItemBlock;
import shordinger.wrapper.net.minecraft.item.ItemBow;
import shordinger.wrapper.net.minecraft.item.ItemElytra;
import shordinger.wrapper.net.minecraft.item.ItemFishingRod;
import shordinger.wrapper.net.minecraft.item.ItemSkull;
import shordinger.wrapper.net.minecraft.item.ItemSword;
import shordinger.wrapper.net.minecraft.item.ItemTool;

public enum EnumEnchantmentType {

    ALL {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            for (EnumEnchantmentType enumenchantmenttype : EnumEnchantmentType.values()) {
                if (enumenchantmenttype != EnumEnchantmentType.ALL && enumenchantmenttype.canEnchantItem(itemIn)) {
                    return true;
                }
            }

            return false;
        }
    },
    ARMOR {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemArmor;
        }
    },
    ARMOR_FEET {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemArmor && ((ItemArmor) itemIn).armorType == EntityEquipmentSlot.FEET;
        }
    },
    ARMOR_LEGS {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemArmor && ((ItemArmor) itemIn).armorType == EntityEquipmentSlot.LEGS;
        }
    },
    ARMOR_CHEST {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemArmor && ((ItemArmor) itemIn).armorType == EntityEquipmentSlot.CHEST;
        }
    },
    ARMOR_HEAD {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemArmor && ((ItemArmor) itemIn).armorType == EntityEquipmentSlot.HEAD;
        }
    },
    WEAPON {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemSword;
        }
    },
    DIGGER {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemTool;
        }
    },
    FISHING_ROD {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemFishingRod;
        }
    },
    BREAKABLE {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn.isDamageable();
        }
    },
    BOW {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            return itemIn instanceof ItemBow;
        }
    },
    WEARABLE {
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn) {
            boolean flag = itemIn instanceof ItemBlock && ((ItemBlock) itemIn).getBlock() instanceof BlockPumpkin;
            return itemIn instanceof ItemArmor || itemIn instanceof ItemElytra || itemIn instanceof ItemSkull || flag;
        }
    };

    private EnumEnchantmentType() {
    }

    private com.google.common.base.Predicate<Item> delegate = null;

    private EnumEnchantmentType(com.google.common.base.Predicate<Item> delegate) {
        this.delegate = delegate;
    }

    /**
     * Return true if the item passed can be enchanted by a enchantment of this type.
     */
    public boolean canEnchantItem(Item itemIn) {
        return this.delegate == null ? false : this.delegate.apply(itemIn);
    }
}
