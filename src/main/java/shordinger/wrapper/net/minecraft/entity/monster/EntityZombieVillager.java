package shordinger.wrapper.net.minecraft.entity.monster;

import java.util.UUID;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.advancements.CriteriaTriggers;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.passive.EntityVillager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityZombieVillager extends EntityZombie {

    private static final DataParameter<Boolean> CONVERTING = EntityDataManager
        .<Boolean>createKey(EntityZombieVillager.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> PROFESSION = EntityDataManager
        .<Integer>createKey(EntityZombieVillager.class, DataSerializers.VARINT);
    /**
     * Ticker used to determine the time remaining for this zombie to convert into a villager when cured.
     */
    private int conversionTime;
    /**
     * The entity that started the conversion, used for the {@link CriteriaTriggers#CURED_ZOMBIE_VILLAGER} advancement
     * criteria
     */
    private UUID converstionStarter;

    public EntityZombieVillager(World worldIn) {
        super(worldIn);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CONVERTING, Boolean.valueOf(false));
        this.dataManager.register(PROFESSION, Integer.valueOf(0));
    }

    public void setProfession(int profession) {
        this.dataManager.set(PROFESSION, Integer.valueOf(profession));
        net.minecraftforge.fml.common.registry.VillagerRegistry.onSetProfession(this, profession);
    }

    // Use Forge Variant below
    @Deprecated
    public int getProfession() {
        return Math.max(((Integer) this.dataManager.get(PROFESSION)).intValue(), 0);
    }

    public static void registerFixesZombieVillager(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityZombieVillager.class);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Profession", this.getProfession());
        compound.setString(
            "ProfessionName",
            this.getForgeProfession()
                .getRegistryName()
                .toString());
        compound.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);

        if (this.converstionStarter != null) {
            compound.setUniqueId("ConversionPlayer", this.converstionStarter);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setProfession(compound.getInteger("Profession"));
        if (compound.hasKey("ProfessionName")) {
            net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession p = net.minecraftforge.fml.common.registry.ForgeRegistries.VILLAGER_PROFESSIONS
                .getValue(new net.minecraft.util.ResourceLocation(compound.getString("ProfessionName")));
            if (p == null) p = net.minecraftforge.fml.common.registry.VillagerRegistry.FARMER;
            this.setForgeProfession(p);
        }

        if (compound.hasKey("ConversionTime", 99) && compound.getInteger("ConversionTime") > -1) {
            this.startConverting(
                compound.hasUniqueId("ConversionPlayer") ? compound.getUniqueId("ConversionPlayer") : null,
                compound.getInteger("ConversionTime"));
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
        net.minecraftforge.fml.common.registry.VillagerRegistry.setRandomProfession(this, this.world.rand);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        if (!this.world.isRemote && this.isConverting()) {
            int i = this.getConversionProgress();
            this.conversionTime -= i;

            if (this.conversionTime <= 0) {
                this.finishConversion();
            }
        }

        super.onUpdate();
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.getItem() == Items.GOLDEN_APPLE && itemstack.getMetadata() == 0
            && this.isPotionActive(MobEffects.WEAKNESS)) {
            if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }

            if (!this.world.isRemote) {
                this.startConverting(player.getUniqueID(), this.rand.nextInt(2401) + 3600);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn() {
        return !this.isConverting();
    }

    /**
     * Returns whether this zombie is in the process of converting to a villager
     */
    public boolean isConverting() {
        return ((Boolean) this.getDataManager()
            .get(CONVERTING)).booleanValue();
    }

    /**
     * Starts conversion of this zombie villager to a villager
     *
     * @param conversionStarterIn The entity that started the conversion's UUID
     * @param conversionTimeIn    The time that it will take to finish conversion
     */
    protected void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn) {
        this.converstionStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
        this.getDataManager()
            .set(CONVERTING, Boolean.valueOf(true));
        this.removePotionEffect(MobEffects.WEAKNESS);
        this.addPotionEffect(
            new PotionEffect(
                MobEffects.STRENGTH,
                conversionTimeIn,
                Math.min(
                    this.world.getDifficulty()
                        .getDifficultyId() - 1,
                    0)));
        this.world.setEntityState(this, (byte) 16);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 16) {
            if (!this.isSilent()) {
                this.world.playSound(
                    this.posX + 0.5D,
                    this.posY + 0.5D,
                    this.posZ + 0.5D,
                    SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE,
                    this.getSoundCategory(),
                    1.0F + this.rand.nextFloat(),
                    this.rand.nextFloat() * 0.7F + 0.3F,
                    false);
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    protected void finishConversion() {
        EntityVillager entityvillager = new EntityVillager(this.world);
        entityvillager.copyLocationAndAnglesFrom(this);
        entityvillager.setProfession(this.getForgeProfession());
        entityvillager.finalizeMobSpawn(
            this.world.getDifficultyForLocation(new BlockPos(entityvillager)),
            (IEntityLivingData) null,
            false);
        entityvillager.setLookingForHome();

        if (this.isChild()) {
            entityvillager.setGrowingAge(-24000);
        }

        this.world.removeEntity(this);
        entityvillager.setNoAI(this.isAIDisabled());

        if (this.hasCustomName()) {
            entityvillager.setCustomNameTag(this.getCustomNameTag());
            entityvillager.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
        }

        this.world.spawnEntity(entityvillager);

        if (this.converstionStarter != null) {
            EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.converstionStarter);

            if (entityplayer instanceof EntityPlayerMP) {
                CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((EntityPlayerMP) entityplayer, this, entityvillager);
            }
        }

        entityvillager.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
        this.world
            .playEvent((EntityPlayer) null, 1027, new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ), 0);
    }

    protected int getConversionProgress() {
        int i = 1;

        if (this.rand.nextFloat() < 0.01F) {
            int j = 0;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int k = (int) this.posX - 4; k < (int) this.posX + 4 && j < 14; ++k) {
                for (int l = (int) this.posY - 4; l < (int) this.posY + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.posZ - 4; i1 < (int) this.posZ + 4 && j < 14; ++i1) {
                        Block block = this.world.getBlockState(blockpos$mutableblockpos.setPos(k, l, i1))
                            .getBlock();

                        if (block == Blocks.IRON_BARS || block == Blocks.BED) {
                            if (this.rand.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch() {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.0F
            : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }

    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    public SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }

    public SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_ZOMBIE_VILLAGER;
    }

    protected ItemStack getSkullDrop() {
        return ItemStack.EMPTY;
    }

    /* ======================================== FORGE START ===================================== */

    @Nullable
    private net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof;

    public void setForgeProfession(net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession prof) {
        this.prof = prof;
        this.setProfession(net.minecraftforge.fml.common.registry.VillagerRegistry.getId(prof));
    }

    public net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession getForgeProfession() {
        if (this.prof == null) {
            this.prof = net.minecraftforge.fml.common.registry.VillagerRegistry.getById(this.getProfession());
            if (this.prof == null) return net.minecraftforge.fml.common.registry.VillagerRegistry.FARMER;
        }
        return this.prof;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (key.equals(PROFESSION)) {
            net.minecraftforge.fml.common.registry.VillagerRegistry
                .onSetProfession(this, this.dataManager.get(PROFESSION));
        }
    }

    /* ======================================== FORGE END ===================================== */
}
