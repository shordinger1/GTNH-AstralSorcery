package shordinger.wrapper.net.minecraft.item;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import shordinger.wrapper.net.minecraft.block.BlockDispenser;
import shordinger.wrapper.net.minecraft.creativetab.CreativeTabs;
import shordinger.wrapper.net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import shordinger.wrapper.net.minecraft.dispenser.IBehaviorDispenseItem;
import shordinger.wrapper.net.minecraft.dispenser.IBlockSource;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.attributes.AttributeModifier;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.util.ActionResult;
import shordinger.wrapper.net.minecraft.util.EntitySelectors;
import shordinger.wrapper.net.minecraft.util.EnumActionResult;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;

public class ItemArmor extends Item {

    /**
     * Holds the 'base' maxDamage that each armorType have.
     */
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
        UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
        UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
        UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final String[] EMPTY_SLOT_NAMES = new String[]{"minecraft:items/empty_armor_slot_boots",
        "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_chestplate",
        "minecraft:items/empty_armor_slot_helmet"};
    public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {

        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            ItemStack itemstack = ItemArmor.dispenseArmor(source, stack);
            return itemstack.isEmpty() ? super.dispenseStack(source, stack) : itemstack;
        }
    };
    /**
     * Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots
     */
    public final EntityEquipmentSlot armorType;
    /**
     * Holds the amount of damage that the armor reduces at full durability.
     */
    public final int damageReduceAmount;
    public final float toughness;
    /**
     * Used on RenderPlayer to select the correspondent armor to be rendered on the player: 0 is cloth, 1 is chain, 2 is
     * iron, 3 is diamond and 4 is gold.
     */
    public final int renderIndex;
    /**
     * The EnumArmorMaterial used for this ItemArmor
     */
    private final ItemArmor.ArmorMaterial material;

    public static ItemStack dispenseArmor(IBlockSource blockSource, ItemStack stack) {
        BlockPos blockpos = blockSource.getBlockPos()
            .offset(
                (EnumFacing) blockSource.getBlockState()
                    .getValue(BlockDispenser.FACING));
        List<EntityLivingBase> list = blockSource.getWorld()
            .<EntityLivingBase>getEntitiesWithinAABB(
                EntityLivingBase.class,
                new AxisAlignedBB(blockpos),
                Predicates.and(EntitySelectors.NOT_SPECTATING, new EntitySelectors.ArmoredMob(stack)));

        if (list.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            EntityLivingBase entitylivingbase = list.get(0);
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(stack);
            ItemStack itemstack = stack.splitStack(1);
            entitylivingbase.setItemStackToSlot(entityequipmentslot, itemstack);

            if (entitylivingbase instanceof EntityLiving) {
                ((EntityLiving) entitylivingbase).setDropChance(entityequipmentslot, 2.0F);
            }

            return stack;
        }
    }

    public ItemArmor(ItemArmor.ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        this.material = materialIn;
        this.armorType = equipmentSlotIn;
        this.renderIndex = renderIndexIn;
        this.damageReduceAmount = materialIn.getDamageReductionAmount(equipmentSlotIn);
        this.setMaxDamage(materialIn.getDurability(equipmentSlotIn));
        this.toughness = materialIn.getToughness();
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.COMBAT);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSER_BEHAVIOR);
    }

    /**
     * Gets the equipment slot of this armor piece (formerly known as armor type)
     */
    @SideOnly(Side.CLIENT)
    public EntityEquipmentSlot getEquipmentSlot() {
        return this.armorType;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability() {
        return this.material.getEnchantability();
    }

    /**
     * Return the armor material for this armor item.
     */
    public ItemArmor.ArmorMaterial getArmorMaterial() {
        return this.material;
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    public boolean hasColor(ItemStack stack) {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
            return false;
        } else {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            return nbttagcompound != null && nbttagcompound.hasKey("display", 10)
                ? nbttagcompound.getCompoundTag("display")
                .hasKey("color", 3)
                : false;
        }
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    public int getColor(ItemStack stack) {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
            return 16777215;
        } else {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3)) {
                    return nbttagcompound1.getInteger("color");
                }
            }

            return 10511680;
        }
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    public void removeColor(ItemStack stack) {
        if (this.material == ItemArmor.ArmorMaterial.LEATHER) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null) {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

                if (nbttagcompound1.hasKey("color")) {
                    nbttagcompound1.removeTag("color");
                }
            }
        }
    }

    /**
     * Sets the color of the specified armor ItemStack
     */
    public void setColor(ItemStack stack, int color) {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER) {
            throw new UnsupportedOperationException("Can't dye non-leather!");
        } else {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound == null) {
                nbttagcompound = new NBTTagCompound();
                stack.setTagCompound(nbttagcompound);
            }

            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

            if (!nbttagcompound.hasKey("display", 10)) {
                nbttagcompound.setTag("display", nbttagcompound1);
            }

            nbttagcompound1.setInteger("color", color);
        }
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param toRepair the {@code ItemStack} being repaired
     * @param repair   the {@code ItemStack} being used to perform the repair
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = this.material.getRepairItemStack();
        if (!mat.isEmpty() && OreDictionary.itemMatches(mat.getRealItemStack(), repair.getRealItemStack(), false))
            return true;
        return super.getIsRepairable(toRepair, repair);
    }

    /**
     * Called when the equipped item is right-clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(entityequipmentslot);

        if (itemstack1.isEmpty()) {
            playerIn.setItemStackToSlot(entityequipmentslot, itemstack.copy());
            itemstack.setCount(0);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == this.armorType) {
            multimap.put(
                SharedMonsterAttributes.ARMOR.getName(),
                new AttributeModifier(
                    ARMOR_MODIFIERS[equipmentSlot.getIndex()],
                    "Armor modifier",
                    (double) this.damageReduceAmount,
                    0));
            multimap.put(
                SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
                new AttributeModifier(
                    ARMOR_MODIFIERS[equipmentSlot.getIndex()],
                    "Armor toughness",
                    (double) this.toughness,
                    0));
        }

        return multimap;
    }

    /**
     * Determines if this armor will be rendered with the secondary 'overlay' texture.
     * If this is true, the first texture will be rendered using a tint of the color
     * specified by getColor(ItemStack)
     *
     * @param stack The stack
     * @return true/false
     */
    public boolean hasOverlay(ItemStack stack) {
        return this.material == ItemArmor.ArmorMaterial.LEATHER || getColor(stack) != 0x00FFFFFF;
    }

    public static enum ArmorMaterial {

        LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F),
        CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F),
        IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F),
        GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F),
        DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);

        private final String name;
        /**
         * Holds the maximum damage factor (each piece multiply this by it's own value) of the material, this is the
         * item damage (how much can absorb before breaks)
         */
        private final int maxDamageFactor;
        /**
         * Holds the damage reduction (each 1 points is half a shield on gui) of each piece of armor (helmet, plate,
         * legs and boots)
         */
        private final int[] damageReductionAmountArray;
        /**
         * Return the enchantability factor of the material
         */
        private final int enchantability;
        private final SoundEvent soundEvent;
        private final float toughness;
        // Added by forge for custom Armor materials.
        public ItemStack repairMaterial = ItemStack.EMPTY;

        private ArmorMaterial(String nameIn, int maxDamageFactorIn, int[] damageReductionAmountArrayIn,
                              int enchantabilityIn, SoundEvent soundEventIn, float toughnessIn) {
            this.name = nameIn;
            this.maxDamageFactor = maxDamageFactorIn;
            this.damageReductionAmountArray = damageReductionAmountArrayIn;
            this.enchantability = enchantabilityIn;
            this.soundEvent = soundEventIn;
            this.toughness = toughnessIn;
        }

        /**
         * Returns the durability for a armor slot of for this type.
         */
        public int getDurability(EntityEquipmentSlot armorType) {
            return ItemArmor.MAX_DAMAGE_ARRAY[armorType.getIndex()] * this.maxDamageFactor;
        }

        /**
         * Return the damage reduction (each 1 point is a half a shield on gui) of the piece index passed (0 = helmet, 1
         * = plate, 2 = legs and 3 = boots)
         */
        public int getDamageReductionAmount(EntityEquipmentSlot armorType) {
            return this.damageReductionAmountArray[armorType.getIndex()];
        }

        /**
         * Return the enchantability factor of the material.
         */
        public int getEnchantability() {
            return this.enchantability;
        }

        public SoundEvent getSoundEvent() {
            return this.soundEvent;
        }

        /**
         * Get a main crafting component of this Armor Material (example is Items.iron_ingot)
         */
        @Deprecated // Use getRepairItemStack below
        public Item getRepairItem() {
            if (this == LEATHER) {
                return Items.LEATHER;
            } else if (this == CHAIN) {
                return Items.IRON_INGOT;
            } else if (this == GOLD) {
                return Items.GOLD_INGOT;
            } else if (this == IRON) {
                return Items.IRON_INGOT;
            } else {
                return this == DIAMOND ? Items.DIAMOND : null;
            }
        }

        @SideOnly(Side.CLIENT)
        public String getName() {
            return this.name;
        }

        public float getToughness() {
            return this.toughness;
        }

        public ArmorMaterial setRepairItem(ItemStack stack) {
            if (!this.repairMaterial.isEmpty()) throw new RuntimeException("Repair material has already been set");
            if (this == LEATHER || this == CHAIN || this == GOLD || this == IRON || this == DIAMOND)
                throw new RuntimeException("Can not change vanilla armor repair materials");
            this.repairMaterial = stack;
            return this;
        }

        public ItemStack getRepairItemStack() {
            if (!repairMaterial.isEmpty()) return repairMaterial;
            Item ret = this.getRepairItem();
            if (ret != null)
                repairMaterial = new ItemStack(ret, 1, net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE);
            return repairMaterial;
        }
    }
}
