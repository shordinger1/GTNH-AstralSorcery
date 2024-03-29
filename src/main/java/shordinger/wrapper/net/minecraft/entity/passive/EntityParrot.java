package shordinger.wrapper.net.minecraft.entity.passive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockLeaves;
import shordinger.wrapper.net.minecraft.block.BlockLog;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityAgeable;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.IEntityLivingData;
import shordinger.wrapper.net.minecraft.entity.SharedMonsterAttributes;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIFollow;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAILandOnOwnersShoulder;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIPanic;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAISit;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAISwimming;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import shordinger.wrapper.net.minecraft.entity.ai.EntityAIWatchClosest;
import shordinger.wrapper.net.minecraft.entity.ai.EntityFlyHelper;
import shordinger.wrapper.net.minecraft.entity.boss.EntityDragon;
import shordinger.wrapper.net.minecraft.entity.boss.EntityWither;
import shordinger.wrapper.net.minecraft.entity.monster.EntityBlaze;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCaveSpider;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCreeper;
import shordinger.wrapper.net.minecraft.entity.monster.EntityElderGuardian;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEnderman;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEndermite;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEvoker;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGhast;
import shordinger.wrapper.net.minecraft.entity.monster.EntityHusk;
import shordinger.wrapper.net.minecraft.entity.monster.EntityIllusionIllager;
import shordinger.wrapper.net.minecraft.entity.monster.EntityMagmaCube;
import shordinger.wrapper.net.minecraft.entity.monster.EntityPigZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityPolarBear;
import shordinger.wrapper.net.minecraft.entity.monster.EntityShulker;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySilverfish;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySkeleton;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySlime;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySpider;
import shordinger.wrapper.net.minecraft.entity.monster.EntityStray;
import shordinger.wrapper.net.minecraft.entity.monster.EntityVex;
import shordinger.wrapper.net.minecraft.entity.monster.EntityVindicator;
import shordinger.wrapper.net.minecraft.entity.monster.EntityWitch;
import shordinger.wrapper.net.minecraft.entity.monster.EntityWitherSkeleton;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombieVillager;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.MobEffects;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.pathfinding.PathNavigate;
import shordinger.wrapper.net.minecraft.pathfinding.PathNavigateFlying;
import shordinger.wrapper.net.minecraft.potion.PotionEffect;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.world.DifficultyInstance;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityParrot extends EntityShoulderRiding implements EntityFlying {

    private static final DataParameter<Integer> VARIANT = EntityDataManager
        .<Integer>createKey(EntityParrot.class, DataSerializers.VARINT);
    /**
     * Used to select entities the parrot can mimic the sound of
     */
    private static final Predicate<EntityLiving> CAN_MIMIC = new Predicate<EntityLiving>() {

        public boolean apply(@Nullable EntityLiving p_apply_1_) {
            return p_apply_1_ != null && EntityParrot.MIMIC_SOUNDS.containsKey(p_apply_1_.getClass());
        }
    };
    private static final Item DEADLY_ITEM = Items.COOKIE;
    private static final Set<Item> TAME_ITEMS = Sets
        .newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
    private static final java.util.Map<Class<? extends Entity>, SoundEvent> MIMIC_SOUNDS = Maps
        .newHashMapWithExpectedSize(32);
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    private boolean partyParrot;
    private BlockPos jukeboxPosition;

    public EntityParrot(World worldIn) {
        super(worldIn);
        this.setSize(0.5F, 0.9F);
        this.moveHelper = new EntityFlyHelper(this);
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
        this.setVariant(this.rand.nextInt(5));
        return super.onInitialSpawn(difficulty, livingdata);
    }

    protected void initEntityAI() {
        this.aiSit = new EntityAISit(this);
        this.tasks.addTask(0, new EntityAIPanic(this, 1.25D));
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(2, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
        this.tasks.addTask(2, new EntityAIWanderAvoidWaterFlying(this, 1.0D));
        this.tasks.addTask(3, new EntityAILandOnOwnersShoulder(this));
        this.tasks.addTask(3, new EntityAIFollow(this, 1.0D, 3.0F, 7.0F));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap()
            .registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
            .setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED)
            .setBaseValue(0.4000000059604645D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .setBaseValue(0.20000000298023224D);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate createNavigator(World worldIn) {
        PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
        pathnavigateflying.setCanOpenDoors(false);
        pathnavigateflying.setCanFloat(true);
        pathnavigateflying.setCanEnterDoors(true);
        return pathnavigateflying;
    }

    public float getEyeHeight() {
        return this.height * 0.6F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
        playMimicSound(this.world, this);

        if (this.jukeboxPosition == null || this.jukeboxPosition.distanceSq(this.posX, this.posY, this.posZ) > 12.0D
            || this.world.getBlockState(this.jukeboxPosition)
            .getBlock() != Blocks.JUKEBOX) {
            this.partyParrot = false;
            this.jukeboxPosition = null;
        }

        super.onLivingUpdate();
        this.calculateFlapping();
    }

    @SideOnly(Side.CLIENT)
    public void setPartying(BlockPos pos, boolean p_191987_2_) {
        this.jukeboxPosition = pos;
        this.partyParrot = p_191987_2_;
    }

    @SideOnly(Side.CLIENT)
    public boolean isPartying() {
        return this.partyParrot;
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = (float) ((double) this.flapSpeed + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.flapSpeed = MathHelper.clamp(this.flapSpeed, 0.0F, 1.0F);

        if (!this.onGround && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping = (float) ((double) this.flapping * 0.9D);

        if (!this.onGround && this.motionY < 0.0D) {
            this.motionY *= 0.6D;
        }

        this.flap += this.flapping * 2.0F;
    }

    private static boolean playMimicSound(World worldIn, Entity p_192006_1_) {
        if (!p_192006_1_.isSilent() && worldIn.rand.nextInt(50) == 0) {
            List<EntityLiving> list = worldIn.<EntityLiving>getEntitiesWithinAABB(
                EntityLiving.class,
                p_192006_1_.getEntityBoundingBox()
                    .grow(20.0D),
                CAN_MIMIC);

            if (!list.isEmpty()) {
                EntityLiving entityliving = list.get(worldIn.rand.nextInt(list.size()));

                if (!entityliving.isSilent()) {
                    SoundEvent soundevent = MIMIC_SOUNDS.get(entityliving.getClass());
                    worldIn.playSound(
                        (EntityPlayer) null,
                        p_192006_1_.posX,
                        p_192006_1_.posY,
                        p_192006_1_.posZ,
                        soundevent,
                        p_192006_1_.getSoundCategory(),
                        0.7F,
                        getPitch(worldIn.rand));
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!this.isTamed() && TAME_ITEMS.contains(itemstack.getItem())) {
            if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }

            if (!this.isSilent()) {
                this.world.playSound(
                    (EntityPlayer) null,
                    this.posX,
                    this.posY,
                    this.posZ,
                    SoundEvents.ENTITY_PARROT_EAT,
                    this.getSoundCategory(),
                    1.0F,
                    1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            }

            if (!this.world.isRemote) {
                if (this.rand.nextInt(10) == 0
                    && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.setTamedBy(player);
                    this.playTameEffect(true);
                    this.world.setEntityState(this, (byte) 7);
                } else {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte) 6);
                }
            }

            return true;
        } else if (itemstack.getItem() == DEADLY_ITEM) {
            if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }

            this.addPotionEffect(new PotionEffect(MobEffects.POISON, 900));

            if (player.isCreative() || !this.getIsInvulnerable()) {
                this.attackEntityFrom(DamageSource.causePlayerDamage(player), Float.MAX_VALUE);
            }

            return true;
        } else {
            if (!this.world.isRemote && !this.isFlying() && this.isTamed() && this.isOwner(player)) {
                this.aiSit.setSitting(!this.isSitting());
            }

            return super.processInteract(player, hand);
        }
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.getEntityBoundingBox().minY);
        int k = MathHelper.floor(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        Block block = this.world.getBlockState(blockpos.down())
            .getBlock();
        return block instanceof BlockLeaves || block == Blocks.GRASS
            || block instanceof BlockLog
            || block == Blocks.AIR && this.world.getLight(blockpos) > 8 && super.getCanSpawnHere();
    }

    public void fall(float distance, float damageMultiplier) {
    }

    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    public boolean canMateWith(EntityAnimal otherAnimal) {
        return false;
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    public static void playAmbientSound(World worldIn, Entity p_192005_1_) {
        if (!p_192005_1_.isSilent() && !playMimicSound(worldIn, p_192005_1_) && worldIn.rand.nextInt(200) == 0) {
            worldIn.playSound(
                (EntityPlayer) null,
                p_192005_1_.posX,
                p_192005_1_.posY,
                p_192005_1_.posZ,
                getAmbientSound(worldIn.rand),
                p_192005_1_.getSoundCategory(),
                1.0F,
                getPitch(worldIn.rand));
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }

    @Nullable
    public SoundEvent getAmbientSound() {
        return getAmbientSound(this.rand);
    }

    private static SoundEvent getAmbientSound(Random random) {
        if (random.nextInt(1000) == 0) {
            List<SoundEvent> list = new ArrayList<SoundEvent>(MIMIC_SOUNDS.values());
            SoundEvent ret = list.get(random.nextInt(list.size()));
            return ret == null ? SoundEvents.ENTITY_PARROT_AMBIENT : ret;
        } else {
            return SoundEvents.ENTITY_PARROT_AMBIENT;
        }
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PARROT_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PARROT_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
    }

    protected float playFlySound(float p_191954_1_) {
        this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
        return p_191954_1_ + this.flapSpeed / 2.0F;
    }

    protected boolean makeFlySound() {
        return true;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch() {
        return getPitch(this.rand);
    }

    private static float getPitch(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed() {
        return true;
    }

    protected void collideWithEntity(Entity entityIn) {
        if (!(entityIn instanceof EntityPlayer)) {
            super.collideWithEntity(entityIn);
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else {
            if (this.aiSit != null) {
                this.aiSit.setSitting(false);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    public int getVariant() {
        return MathHelper.clamp(((Integer) this.dataManager.get(VARIANT)).intValue(), 0, 4);
    }

    public void setVariant(int p_191997_1_) {
        this.dataManager.set(VARIANT, Integer.valueOf(p_191997_1_));
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Variant", this.getVariant());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setVariant(compound.getInteger("Variant"));
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_PARROT;
    }

    public boolean isFlying() {
        return !this.onGround;
    }

    static {
        registerMimicSound(EntityBlaze.class, SoundEvents.E_PARROT_IM_BLAZE);
        registerMimicSound(EntityCaveSpider.class, SoundEvents.E_PARROT_IM_SPIDER);
        registerMimicSound(EntityCreeper.class, SoundEvents.E_PARROT_IM_CREEPER);
        registerMimicSound(EntityElderGuardian.class, SoundEvents.E_PARROT_IM_ELDER_GUARDIAN);
        registerMimicSound(EntityDragon.class, SoundEvents.E_PARROT_IM_ENDERDRAGON);
        registerMimicSound(EntityEnderman.class, SoundEvents.E_PARROT_IM_ENDERMAN);
        registerMimicSound(EntityEndermite.class, SoundEvents.E_PARROT_IM_ENDERMITE);
        registerMimicSound(EntityEvoker.class, SoundEvents.E_PARROT_IM_EVOCATION_ILLAGER);
        registerMimicSound(EntityGhast.class, SoundEvents.E_PARROT_IM_GHAST);
        registerMimicSound(EntityHusk.class, SoundEvents.E_PARROT_IM_HUSK);
        registerMimicSound(EntityIllusionIllager.class, SoundEvents.E_PARROT_IM_ILLUSION_ILLAGER);
        registerMimicSound(EntityMagmaCube.class, SoundEvents.E_PARROT_IM_MAGMACUBE);
        registerMimicSound(EntityPigZombie.class, SoundEvents.E_PARROT_IM_ZOMBIE_PIGMAN);
        registerMimicSound(EntityPolarBear.class, SoundEvents.E_PARROT_IM_POLAR_BEAR);
        registerMimicSound(EntityShulker.class, SoundEvents.E_PARROT_IM_SHULKER);
        registerMimicSound(EntitySilverfish.class, SoundEvents.E_PARROT_IM_SILVERFISH);
        registerMimicSound(EntitySkeleton.class, SoundEvents.E_PARROT_IM_SKELETON);
        registerMimicSound(EntitySlime.class, SoundEvents.E_PARROT_IM_SLIME);
        registerMimicSound(EntitySpider.class, SoundEvents.E_PARROT_IM_SPIDER);
        registerMimicSound(EntityStray.class, SoundEvents.E_PARROT_IM_STRAY);
        registerMimicSound(EntityVex.class, SoundEvents.E_PARROT_IM_VEX);
        registerMimicSound(EntityVindicator.class, SoundEvents.E_PARROT_IM_VINDICATION_ILLAGER);
        registerMimicSound(EntityWitch.class, SoundEvents.E_PARROT_IM_WITCH);
        registerMimicSound(EntityWither.class, SoundEvents.E_PARROT_IM_WITHER);
        registerMimicSound(EntityWitherSkeleton.class, SoundEvents.E_PARROT_IM_WITHER_SKELETON);
        registerMimicSound(EntityWolf.class, SoundEvents.E_PARROT_IM_WOLF);
        registerMimicSound(EntityZombie.class, SoundEvents.E_PARROT_IM_ZOMBIE);
        registerMimicSound(EntityZombieVillager.class, SoundEvents.E_PARROT_IM_ZOMBIE_VILLAGER);
    }

    public static void registerMimicSound(Class<? extends Entity> cls, SoundEvent sound) {
        MIMIC_SOUNDS.put(cls, sound);
    }
}
