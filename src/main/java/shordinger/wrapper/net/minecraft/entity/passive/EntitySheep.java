package shordinger.wrapper.net.minecraft.entity.passive;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.entity.EntityAgeable;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIEatGrass;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIFollowParent;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAILookIdle;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIMate;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIPanic;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAISwimming;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAITempt;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWatchClosest;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.inventory.Container;
import shordinger.wrapper.net.minecraft.inventory.InventoryCrafting;
import shordinger.wrapper.net.minecraft.item.EnumDyeColor;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntitySheep extends EntityAnimal implements net.minecraftforge.common.IShearable {

    private static final DataParameter<Byte> DYE_COLOR = EntityDataManager
        .<Byte>createKey(EntitySheep.class, DataSerializers.BYTE);
    /**
     * Internal crafting inventory used to check the result of mixing dyes corresponding to the fleece color when
     * breeding sheep.
     */
    private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {

        /**
         * Determines whether supplied player can use this container
         */
        public boolean canInteractWith(EntityPlayer playerIn) {
            return false;
        }
    }, 2, 1);
    /**
     * Map from EnumDyeColor to RGB values for passage to GlStateManager.color()
     */
    private static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);
    /**
     * Used to control movement as well as wool regrowth. Set to 40 on handleHealthUpdate and counts down with each
     * tick.
     */
    private int sheepTimer;
    private EntityAIEatGrass entityAIEatGrass;

    private static float[] createSheepColor(EnumDyeColor p_192020_0_) {
        float[] afloat = p_192020_0_.getColorComponentValues();
        float f = 0.75F;
        return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
    }

    @SideOnly(Side.CLIENT)
    public static float[] getDyeRgb(EnumDyeColor dyeColor) {
        return DYE_TO_RGB.get(dyeColor);
    }

    public EntitySheep(World worldIn) {
        super(worldIn);
        this.setSize(0.9F, 1.3F);
        this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.DYE));
        this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.DYE));
    }

    protected void initEntityAI() {
        this.entityAIEatGrass = new EntityAIEatGrass(this);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Items.WHEAT, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, this.entityAIEatGrass);
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    protected void updateAITasks() {
        this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
        super.updateAITasks();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        if (this.world.isRemote) {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        super.onLivingUpdate();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.23000000417232513D);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DYE_COLOR, Byte.valueOf((byte) 0));
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        if (this.getSheared()) {
            return LootTableList.ENTITIES_SHEEP;
        } else {
            switch (this.getFleeceColor()) {
                case WHITE:
                default:
                    return LootTableList.ENTITIES_SHEEP_WHITE;
                case ORANGE:
                    return LootTableList.ENTITIES_SHEEP_ORANGE;
                case MAGENTA:
                    return LootTableList.ENTITIES_SHEEP_MAGENTA;
                case LIGHT_BLUE:
                    return LootTableList.ENTITIES_SHEEP_LIGHT_BLUE;
                case YELLOW:
                    return LootTableList.ENTITIES_SHEEP_YELLOW;
                case LIME:
                    return LootTableList.ENTITIES_SHEEP_LIME;
                case PINK:
                    return LootTableList.ENTITIES_SHEEP_PINK;
                case GRAY:
                    return LootTableList.ENTITIES_SHEEP_GRAY;
                case SILVER:
                    return LootTableList.ENTITIES_SHEEP_SILVER;
                case CYAN:
                    return LootTableList.ENTITIES_SHEEP_CYAN;
                case PURPLE:
                    return LootTableList.ENTITIES_SHEEP_PURPLE;
                case BLUE:
                    return LootTableList.ENTITIES_SHEEP_BLUE;
                case BROWN:
                    return LootTableList.ENTITIES_SHEEP_BROWN;
                case GREEN:
                    return LootTableList.ENTITIES_SHEEP_GREEN;
                case RED:
                    return LootTableList.ENTITIES_SHEEP_RED;
                case BLACK:
                    return LootTableList.ENTITIES_SHEEP_BLACK;
            }
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 10) {
            this.sheepTimer = 40;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (false && itemstack.getItem() == Items.SHEARS && !this.getSheared() && !this.isChild()) // Forge: Moved to
        // onSheared
        {
            if (!this.world.isRemote) {
                this.setSheared(true);
                int i = 1 + this.rand.nextInt(3);

                for (int j = 0; j < i; ++j) {
                    EntityItem entityitem = this.entityDropItem(
                        new ItemStack(
                            Item.getItemFromBlock(Blocks.WOOL),
                            1,
                            this.getFleeceColor()
                                .getMetadata()),
                        1.0F);
                    entityitem.motionY += (double) (this.rand.nextFloat() * 0.05F);
                    entityitem.motionX += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                    entityitem.motionZ += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                }
            }

            itemstack.damageItem(1, player);
            this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        }

        return super.processInteract(player, hand);
    }

    public static void registerFixesSheep(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntitySheep.class);
    }

    @SideOnly(Side.CLIENT)
    public float getHeadRotationPointY(float p_70894_1_) {
        if (this.sheepTimer <= 0) {
            return 0.0F;
        } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36) {
            return 1.0F;
        } else {
            return this.sheepTimer < 4 ? ((float) this.sheepTimer - p_70894_1_) / 4.0F
                : -((float) (this.sheepTimer - 40) - p_70894_1_) / 4.0F;
        }
    }

    @SideOnly(Side.CLIENT)
    public float getHeadRotationAngleX(float p_70890_1_) {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
            float f = ((float) (this.sheepTimer - 4) - p_70890_1_) / 32.0F;
            return ((float) Math.PI / 5F) + ((float) Math.PI * 7F / 100F) * MathHelper.sin(f * 28.7F);
        } else {
            return this.sheepTimer > 0 ? ((float) Math.PI / 5F) : this.rotationPitch * 0.017453292F;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sheared", this.getSheared());
        compound.setByte(
            "Color",
            (byte) this.getFleeceColor()
                .getMetadata());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setSheared(compound.getBoolean("Sheared"));
        this.setFleeceColor(EnumDyeColor.byMetadata(compound.getByte("Color")));
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SHEEP_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SHEEP_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SHEEP_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    /**
     * Gets the wool color of this sheep.
     */
    public EnumDyeColor getFleeceColor() {
        return EnumDyeColor.byMetadata(((Byte) this.dataManager.get(DYE_COLOR)).byteValue() & 15);
    }

    /**
     * Sets the wool color of this sheep
     */
    public void setFleeceColor(EnumDyeColor color) {
        byte b0 = ((Byte) this.dataManager.get(DYE_COLOR)).byteValue();
        this.dataManager.set(DYE_COLOR, Byte.valueOf((byte) (b0 & 240 | color.getMetadata() & 15)));
    }

    /**
     * returns true if a sheeps wool has been sheared
     */
    public boolean getSheared() {
        return (((Byte) this.dataManager.get(DYE_COLOR)).byteValue() & 16) != 0;
    }

    /**
     * make a sheep sheared if set to true
     */
    public void setSheared(boolean sheared) {
        byte b0 = ((Byte) this.dataManager.get(DYE_COLOR)).byteValue();

        if (sheared) {
            this.dataManager.set(DYE_COLOR, Byte.valueOf((byte) (b0 | 16)));
        } else {
            this.dataManager.set(DYE_COLOR, Byte.valueOf((byte) (b0 & -17)));
        }
    }

    /**
     * Chooses a "vanilla" sheep color based on the provided random.
     */
    public static EnumDyeColor getRandomSheepColor(Random random) {
        int i = random.nextInt(100);

        if (i < 5) {
            return EnumDyeColor.BLACK;
        } else if (i < 10) {
            return EnumDyeColor.GRAY;
        } else if (i < 15) {
            return EnumDyeColor.SILVER;
        } else if (i < 18) {
            return EnumDyeColor.BROWN;
        } else {
            return random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE;
        }
    }

    public EntitySheep createChild(EntityAgeable ageable) {
        EntitySheep entitysheep = (EntitySheep) ageable;
        EntitySheep entitysheep1 = new EntitySheep(this.world);
        entitysheep1.setFleeceColor(this.getDyeColorMixFromParents(this, entitysheep));
        return entitysheep1;
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void eatGrassBonus() {
        this.setSheared(false);

        if (this.isChild()) {
            this.addGrowth(60);
        }
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory.
     * <p>
     * The livingdata parameter is used to pass data between all instances during a pack spawn. It will be null on the
     * first call. Subclasses may check if it's null, and then create a new one and return it if so, initializing all
     * entities in the pack with the contained data.
     *
     * @param difficulty The current local difficulty
     * @param livingdata Shared spawn data. Will usually be null. (See return value for more information)
     * @return The IEntityLivingData to pass to this method for other instances of this entity class within the same
     * pack
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setFleeceColor(getRandomSheepColor(this.world.rand));
        return livingdata;
    }

    @Override
    public boolean isShearable(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos) {
        return !this.getSheared() && !this.isChild();
    }

    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos,
                                               int fortune) {
        this.setSheared(true);
        int i = 1 + this.rand.nextInt(3);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int j = 0; j < i; ++j)
            ret.add(
                new ItemStack(
                    Item.getItemFromBlock(Blocks.WOOL),
                    1,
                    this.getFleeceColor()
                        .getMetadata()));

        this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    /**
     * Attempts to mix both parent sheep to come up with a mixed dye color.
     */
    private EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother) {
        int i = ((EntitySheep) father).getFleeceColor()
            .getDyeDamage();
        int j = ((EntitySheep) mother).getFleeceColor()
            .getDyeDamage();
        this.inventoryCrafting.getStackInSlot(0)
            .setItemDamage(i);
        this.inventoryCrafting.getStackInSlot(1)
            .setItemDamage(j);
        ItemStack itemstack = CraftingManager.findMatchingResult(this.inventoryCrafting, ((EntitySheep) father).world);
        int k;

        if (itemstack.getItem() == Items.DYE) {
            k = itemstack.getMetadata();
        } else {
            k = this.world.rand.nextBoolean() ? i : j;
        }

        return EnumDyeColor.byDyeDamage(k);
    }

    public float getEyeHeight() {
        return 0.95F * this.height;
    }

    static {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
            DYE_TO_RGB.put(enumdyecolor, createSheepColor(enumdyecolor));
        }

        DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[]{0.9019608F, 0.9019608F, 0.9019608F});
    }
}
