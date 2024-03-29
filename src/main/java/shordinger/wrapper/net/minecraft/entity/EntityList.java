package shordinger.wrapper.net.minecraft.entity;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.boss.EntityDragon;
import shordinger.wrapper.net.minecraft.entity.boss.EntityWither;
import shordinger.wrapper.net.minecraft.entity.effect.EntityLightningBolt;
import shordinger.wrapper.net.minecraft.entity.item.EntityArmorStand;
import shordinger.wrapper.net.minecraft.entity.item.EntityBoat;
import shordinger.wrapper.net.minecraft.entity.item.EntityEnderCrystal;
import shordinger.wrapper.net.minecraft.entity.item.EntityEnderEye;
import shordinger.wrapper.net.minecraft.entity.item.EntityEnderPearl;
import shordinger.wrapper.net.minecraft.entity.item.EntityExpBottle;
import shordinger.wrapper.net.minecraft.entity.item.EntityFallingBlock;
import shordinger.wrapper.net.minecraft.entity.item.EntityFireworkRocket;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.entity.item.EntityItemFrame;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecart;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartChest;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartCommandBlock;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartEmpty;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartFurnace;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartHopper;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartMobSpawner;
import shordinger.wrapper.net.minecraft.entity.item.EntityMinecartTNT;
import shordinger.wrapper.net.minecraft.entity.item.EntityPainting;
import shordinger.wrapper.net.minecraft.entity.item.EntityTNTPrimed;
import shordinger.wrapper.net.minecraft.entity.item.EntityXPOrb;
import shordinger.wrapper.net.minecraft.entity.monster.EntityBlaze;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCaveSpider;
import shordinger.wrapper.net.minecraft.entity.monster.EntityCreeper;
import shordinger.wrapper.net.minecraft.entity.monster.EntityElderGuardian;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEnderman;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEndermite;
import shordinger.wrapper.net.minecraft.entity.monster.EntityEvoker;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGhast;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGiantZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityGuardian;
import shordinger.wrapper.net.minecraft.entity.monster.EntityHusk;
import shordinger.wrapper.net.minecraft.entity.monster.EntityIllusionIllager;
import shordinger.wrapper.net.minecraft.entity.monster.EntityIronGolem;
import shordinger.wrapper.net.minecraft.entity.monster.EntityMagmaCube;
import shordinger.wrapper.net.minecraft.entity.monster.EntityPigZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityPolarBear;
import shordinger.wrapper.net.minecraft.entity.monster.EntityShulker;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySilverfish;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySkeleton;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySlime;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySnowman;
import shordinger.wrapper.net.minecraft.entity.monster.EntitySpider;
import shordinger.wrapper.net.minecraft.entity.monster.EntityStray;
import shordinger.wrapper.net.minecraft.entity.monster.EntityVex;
import shordinger.wrapper.net.minecraft.entity.monster.EntityVindicator;
import shordinger.wrapper.net.minecraft.entity.monster.EntityWitch;
import shordinger.wrapper.net.minecraft.entity.monster.EntityWitherSkeleton;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombie;
import shordinger.wrapper.net.minecraft.entity.monster.EntityZombieVillager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityBat;
import shordinger.wrapper.net.minecraft.entity.passive.EntityChicken;
import shordinger.wrapper.net.minecraft.entity.passive.EntityCow;
import shordinger.wrapper.net.minecraft.entity.passive.EntityDonkey;
import shordinger.wrapper.net.minecraft.entity.passive.EntityHorse;
import shordinger.wrapper.net.minecraft.entity.passive.EntityLlama;
import shordinger.wrapper.net.minecraft.entity.passive.EntityMooshroom;
import shordinger.wrapper.net.minecraft.entity.passive.EntityMule;
import shordinger.wrapper.net.minecraft.entity.passive.EntityOcelot;
import shordinger.wrapper.net.minecraft.entity.passive.EntityParrot;
import shordinger.wrapper.net.minecraft.entity.passive.EntityPig;
import shordinger.wrapper.net.minecraft.entity.passive.EntityRabbit;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySheep;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySkeletonHorse;
import shordinger.wrapper.net.minecraft.entity.passive.EntitySquid;
import shordinger.wrapper.net.minecraft.entity.passive.EntityVillager;
import shordinger.wrapper.net.minecraft.entity.passive.EntityWolf;
import shordinger.wrapper.net.minecraft.entity.passive.EntityZombieHorse;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityDragonFireball;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityEgg;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityEvokerFangs;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityLargeFireball;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityLlamaSpit;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityPotion;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityShulkerBullet;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySmallFireball;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySnowball;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySpectralArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.stats.StatBase;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityList {

    public static final ResourceLocation LIGHTNING_BOLT = new ResourceLocation("lightning_bolt");
    private static final ResourceLocation PLAYER = new ResourceLocation("player");
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * This is a HashMap of the Creative Entity Eggs/Spawners.
     */
    public static final Map<ResourceLocation, EntityList.EntityEggInfo> ENTITY_EGGS = Maps.<ResourceLocation, EntityList.EntityEggInfo>newLinkedHashMap();
    private static final Set<ResourceLocation> EXTRA_NAMES = Sets.newHashSet();

    /**
     * Gets the {@link ResourceLocation} that identifies the given entity's type.
     */
    @Nullable
    public static ResourceLocation getKey(Entity entityIn) {
        return getKey(entityIn.getClass());
    }

    /**
     * Gets the {@link ResourceLocation} that identifies the given entity's type.
     *
     * @return The resource location, or null if the {@link #REGISTRY} does not contain a mapping for that class.
     */
    @Nullable
    public static ResourceLocation getKey(Class<? extends Entity> entityIn) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry
            .getEntry(entityIn);
        return entry == null ? null : entry.getRegistryName();
    }

    /**
     * Gets the original name for the entity, used in versions prior to 1.11. This name is also used for translation
     * strings.
     *
     * @return The entity's original name, or null if the given entity's type is not known.
     */
    @Nullable
    public static String getEntityString(Entity entityIn) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry
            .getEntry(entityIn.getClass());
        return entry == null ? null : entry.getName();
    }

    /**
     * Gets the original name for the given entity type, used in versions prior to 1.11. Note that even entities added
     * after 1.11 have old names as returned by this method.
     * <p>
     * This name is also used for translation strings; translate <code>"entity.$oldid.name"</code> (with
     * <var>$oldid</var> being the result of this method) to get those names. Note that the name is upper-case in most
     * situations.
     *
     * @return The original entity name, or null if there is no known entity for that type.
     */
    @Nullable
    public static String getTranslationName(@Nullable ResourceLocation entityType) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES
            .getValue(entityType);
        return entry == null ? null : entry.getName();
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Class<? extends Entity> getClassFromID(int entityID) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.registries.GameData
            .getEntityRegistry()
            .getValue(entityID);
        return entry == null ? null : entry.getEntityClass();
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Class<? extends Entity> getClassFromName(String p_192839_0_) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES
            .getValue(new ResourceLocation(p_192839_0_));
        return entry == null ? null : entry.getEntityClass();
    }

    public static int getID(Class<? extends Entity> cls) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry
            .getEntry(cls);
        return entry == null ? -1
            : net.minecraftforge.registries.GameData.getEntityRegistry()
            .getID(entry);
    }

    @Nullable
    public static Class<? extends Entity> getClass(ResourceLocation key) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES
            .getValue(key);
        return entry == null ? null : entry.getEntityClass();
    }

    /**
     * Creates a new entity of the given type in the given world.
     *
     * @return The newly created entity, or null if creation failed
     */
    @Nullable
    public static Entity newEntity(@Nullable Class<? extends Entity> clazz, World worldIn) {
        if (clazz == null) {
            return null;
        } else {
            try {
                net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry
                    .getEntry(clazz);
                if (entry != null) return entry.newInstance(worldIn);
                return clazz.getConstructor(World.class)
                    .newInstance(worldIn);
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Creates a new entity with the given numeric networked entity type ID in the given world.
     *
     * @return The newly created entity, or null if creation failed
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    public static Entity createEntityByID(int entityID, World worldIn) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.registries.GameData
            .getEntityRegistry()
            .getValue(entityID);
        return entry == null ? null : entry.newInstance(worldIn);
    }

    /**
     * Creates a new entity of the given type in the given world.
     *
     * @return The newly created entity, or null if creation failed
     */
    @Nullable
    public static Entity createEntityByIDFromName(ResourceLocation name, World worldIn) {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES
            .getValue(name);
        return entry == null ? null : entry.newInstance(worldIn);
    }

    /**
     * Creates a new entity from the given NBT data in the given world.
     * <p>
     * If the entity fails to create, null will be returned and a warning will be logged.
     *
     * @return The newly created entity, or null
     */
    @Nullable
    public static Entity createEntityFromNBT(NBTTagCompound nbt, World worldIn) {
        ResourceLocation resourcelocation = new ResourceLocation(nbt.getString("id"));
        Entity entity = createEntityByIDFromName(resourcelocation, worldIn);

        if (entity == null) {
            LOGGER.warn("Skipping Entity with id {}", (Object) resourcelocation);
        } else {
            try {
                entity.readFromNBT(nbt);
            } catch (Exception e) {
                net.minecraftforge.fml.common.FMLLog.log.error(
                    "An Entity {}({}) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                    nbt.getString("id"),
                    entity.getName(),
                    e);
                entity = null;
            }
        }

        return entity;
    }

    /**
     * Gets a collection of all known entity types.
     *
     * @return A collection of all known entity types. Do not modify this collection.
     */
    public static Set<ResourceLocation> getEntityNameList() {
        return Sets.union(net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getKeys(), EXTRA_NAMES);
    }

    /**
     * Checks if the given entity type matches the type of that entity, correctly handling behavior of players and
     * lightning bolts.
     *
     * @return true if the type matches
     */
    public static boolean isMatchingName(Entity entityIn, ResourceLocation entityName) {
        ResourceLocation resourcelocation = getKey(entityIn.getClass());

        if (resourcelocation != null) {
            return resourcelocation.equals(entityName);
        } else if (entityIn instanceof EntityPlayer) {
            return PLAYER.equals(entityName);
        } else {
            return entityIn instanceof EntityLightningBolt ? LIGHTNING_BOLT.equals(entityName) : false;
        }
    }

    /**
     * Checks if the given resource location matches a registered entity type, correctly handling behavior of players
     * and lightning bols.
     *
     * @return true if the type is an entity.
     */
    public static boolean isRegistered(ResourceLocation entityName) {
        return PLAYER.equals(entityName) || getEntityNameList().contains(entityName);
    }

    public static String getValidTypeNames() {
        StringBuilder stringbuilder = new StringBuilder();

        for (ResourceLocation resourcelocation : getEntityNameList()) {
            stringbuilder.append((Object) resourcelocation)
                .append(", ");
        }

        stringbuilder.append((Object) PLAYER);
        return stringbuilder.toString();
    }

    public static void init() {
        register(1, "item", EntityItem.class, "Item");
        register(2, "xp_orb", EntityXPOrb.class, "XPOrb");
        register(3, "area_effect_cloud", EntityAreaEffectCloud.class, "AreaEffectCloud");
        register(4, "elder_guardian", EntityElderGuardian.class, "ElderGuardian");
        register(5, "wither_skeleton", EntityWitherSkeleton.class, "WitherSkeleton");
        register(6, "stray", EntityStray.class, "Stray");
        register(7, "egg", EntityEgg.class, "ThrownEgg");
        register(8, "leash_knot", EntityLeashKnot.class, "LeashKnot");
        register(9, "painting", EntityPainting.class, "Painting");
        register(10, "arrow", EntityTippedArrow.class, "Arrow");
        register(11, "snowball", EntitySnowball.class, "Snowball");
        register(12, "fireball", EntityLargeFireball.class, "Fireball");
        register(13, "small_fireball", EntitySmallFireball.class, "SmallFireball");
        register(14, "ender_pearl", EntityEnderPearl.class, "ThrownEnderpearl");
        register(15, "eye_of_ender_signal", EntityEnderEye.class, "EyeOfEnderSignal");
        register(16, "potion", EntityPotion.class, "ThrownPotion");
        register(17, "xp_bottle", EntityExpBottle.class, "ThrownExpBottle");
        register(18, "item_frame", EntityItemFrame.class, "ItemFrame");
        register(19, "wither_skull", EntityWitherSkull.class, "WitherSkull");
        register(20, "tnt", EntityTNTPrimed.class, "PrimedTnt");
        register(21, "falling_block", EntityFallingBlock.class, "FallingSand");
        register(22, "fireworks_rocket", EntityFireworkRocket.class, "FireworksRocketEntity");
        register(23, "husk", EntityHusk.class, "Husk");
        register(24, "spectral_arrow", EntitySpectralArrow.class, "SpectralArrow");
        register(25, "shulker_bullet", EntityShulkerBullet.class, "ShulkerBullet");
        register(26, "dragon_fireball", EntityDragonFireball.class, "DragonFireball");
        register(27, "zombie_villager", EntityZombieVillager.class, "ZombieVillager");
        register(28, "skeleton_horse", EntitySkeletonHorse.class, "SkeletonHorse");
        register(29, "zombie_horse", EntityZombieHorse.class, "ZombieHorse");
        register(30, "armor_stand", EntityArmorStand.class, "ArmorStand");
        register(31, "donkey", EntityDonkey.class, "Donkey");
        register(32, "mule", EntityMule.class, "Mule");
        register(33, "evocation_fangs", EntityEvokerFangs.class, "EvocationFangs");
        register(34, "evocation_illager", EntityEvoker.class, "EvocationIllager");
        register(35, "vex", EntityVex.class, "Vex");
        register(36, "vindication_illager", EntityVindicator.class, "VindicationIllager");
        register(37, "illusion_illager", EntityIllusionIllager.class, "IllusionIllager");
        register(
            40,
            "commandblock_minecart",
            EntityMinecartCommandBlock.class,
            EntityMinecart.Type.COMMAND_BLOCK.getName());
        register(41, "boat", EntityBoat.class, "Boat");
        register(42, "minecart", EntityMinecartEmpty.class, EntityMinecart.Type.RIDEABLE.getName());
        register(43, "chest_minecart", EntityMinecartChest.class, EntityMinecart.Type.CHEST.getName());
        register(44, "furnace_minecart", EntityMinecartFurnace.class, EntityMinecart.Type.FURNACE.getName());
        register(45, "tnt_minecart", EntityMinecartTNT.class, EntityMinecart.Type.TNT.getName());
        register(46, "hopper_minecart", EntityMinecartHopper.class, EntityMinecart.Type.HOPPER.getName());
        register(47, "spawner_minecart", EntityMinecartMobSpawner.class, EntityMinecart.Type.SPAWNER.getName());
        register(50, "creeper", EntityCreeper.class, "Creeper");
        register(51, "skeleton", EntitySkeleton.class, "Skeleton");
        register(52, "spider", EntitySpider.class, "Spider");
        register(53, "giant", EntityGiantZombie.class, "Giant");
        register(54, "zombie", EntityZombie.class, "Zombie");
        register(55, "slime", EntitySlime.class, "Slime");
        register(56, "ghast", EntityGhast.class, "Ghast");
        register(57, "zombie_pigman", EntityPigZombie.class, "PigZombie");
        register(58, "enderman", EntityEnderman.class, "Enderman");
        register(59, "cave_spider", EntityCaveSpider.class, "CaveSpider");
        register(60, "silverfish", EntitySilverfish.class, "Silverfish");
        register(61, "blaze", EntityBlaze.class, "Blaze");
        register(62, "magma_cube", EntityMagmaCube.class, "LavaSlime");
        register(63, "ender_dragon", EntityDragon.class, "EnderDragon");
        register(64, "wither", EntityWither.class, "WitherBoss");
        register(65, "bat", EntityBat.class, "Bat");
        register(66, "witch", EntityWitch.class, "Witch");
        register(67, "endermite", EntityEndermite.class, "Endermite");
        register(68, "guardian", EntityGuardian.class, "Guardian");
        register(69, "shulker", EntityShulker.class, "Shulker");
        register(90, "pig", EntityPig.class, "Pig");
        register(91, "sheep", EntitySheep.class, "Sheep");
        register(92, "cow", EntityCow.class, "Cow");
        register(93, "chicken", EntityChicken.class, "Chicken");
        register(94, "squid", EntitySquid.class, "Squid");
        register(95, "wolf", EntityWolf.class, "Wolf");
        register(96, "mooshroom", EntityMooshroom.class, "MushroomCow");
        register(97, "snowman", EntitySnowman.class, "SnowMan");
        register(98, "ocelot", EntityOcelot.class, "Ozelot");
        register(99, "villager_golem", EntityIronGolem.class, "VillagerGolem");
        register(100, "horse", EntityHorse.class, "Horse");
        register(101, "rabbit", EntityRabbit.class, "Rabbit");
        register(102, "polar_bear", EntityPolarBear.class, "PolarBear");
        register(103, "llama", EntityLlama.class, "Llama");
        register(104, "llama_spit", EntityLlamaSpit.class, "LlamaSpit");
        register(105, "parrot", EntityParrot.class, "Parrot");
        register(120, "villager", EntityVillager.class, "Villager");
        register(200, "ender_crystal", EntityEnderCrystal.class, "EnderCrystal");
        addSpawnInfo("bat", 4996656, 986895);
        addSpawnInfo("blaze", 16167425, 16775294);
        addSpawnInfo("cave_spider", 803406, 11013646);
        addSpawnInfo("chicken", 10592673, 16711680);
        addSpawnInfo("cow", 4470310, 10592673);
        addSpawnInfo("creeper", 894731, 0);
        addSpawnInfo("donkey", 5457209, 8811878);
        addSpawnInfo("elder_guardian", 13552826, 7632531);
        addSpawnInfo("enderman", 1447446, 0);
        addSpawnInfo("endermite", 1447446, 7237230);
        addSpawnInfo("evocation_illager", 9804699, 1973274);
        addSpawnInfo("ghast", 16382457, 12369084);
        addSpawnInfo("guardian", 5931634, 15826224);
        addSpawnInfo("horse", 12623485, 15656192);
        addSpawnInfo("husk", 7958625, 15125652);
        addSpawnInfo("llama", 12623485, 10051392);
        addSpawnInfo("magma_cube", 3407872, 16579584);
        addSpawnInfo("mooshroom", 10489616, 12040119);
        addSpawnInfo("mule", 1769984, 5321501);
        addSpawnInfo("ocelot", 15720061, 5653556);
        addSpawnInfo("parrot", 894731, 16711680);
        addSpawnInfo("pig", 15771042, 14377823);
        addSpawnInfo("polar_bear", 15921906, 9803152);
        addSpawnInfo("rabbit", 10051392, 7555121);
        addSpawnInfo("sheep", 15198183, 16758197);
        addSpawnInfo("shulker", 9725844, 5060690);
        addSpawnInfo("silverfish", 7237230, 3158064);
        addSpawnInfo("skeleton", 12698049, 4802889);
        addSpawnInfo("skeleton_horse", 6842447, 15066584);
        addSpawnInfo("slime", 5349438, 8306542);
        addSpawnInfo("spider", 3419431, 11013646);
        addSpawnInfo("squid", 2243405, 7375001);
        addSpawnInfo("stray", 6387319, 14543594);
        addSpawnInfo("vex", 8032420, 15265265);
        addSpawnInfo("villager", 5651507, 12422002);
        addSpawnInfo("vindication_illager", 9804699, 2580065);
        addSpawnInfo("witch", 3407872, 5349438);
        addSpawnInfo("wither_skeleton", 1315860, 4672845);
        addSpawnInfo("wolf", 14144467, 13545366);
        addSpawnInfo("zombie", 44975, 7969893);
        addSpawnInfo("zombie_horse", 3232308, 9945732);
        addSpawnInfo("zombie_pigman", 15373203, 5009705);
        addSpawnInfo("zombie_villager", 5651507, 7969893);
        EXTRA_NAMES.add(LIGHTNING_BOLT);
    }

    private static void register(int id, String name, Class<? extends Entity> clazz, String oldName) {
        try {
            clazz.getConstructor(World.class);
        } catch (NoSuchMethodException var5) {
            throw new RuntimeException("Invalid class " + clazz + " no constructor taking " + World.class.getName());
        }

        if ((clazz.getModifiers() & 1024) == 1024) {
            throw new RuntimeException("Invalid abstract class " + clazz);
        } else {
            ResourceLocation resourcelocation = new ResourceLocation(name);
            net.minecraftforge.registries.GameData.registerEntity(id, resourcelocation, clazz, oldName);
        }
    }

    protected static EntityList.EntityEggInfo addSpawnInfo(String id, int primaryColor, int secondaryColor) {
        ResourceLocation resourcelocation = new ResourceLocation(id);
        EntityList.EntityEggInfo egg = new EntityList.EntityEggInfo(resourcelocation, primaryColor, secondaryColor);
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES
            .getValue(resourcelocation);
        if (entry != null) entry.setEgg(egg);
        return (EntityList.EntityEggInfo) ENTITY_EGGS.put(resourcelocation, egg);
    }

    public static class EntityEggInfo {

        /**
         * The entityID of the spawned mob
         */
        public final ResourceLocation spawnedID;
        /**
         * Base color of the egg
         */
        public final int primaryColor;
        /**
         * Color of the egg spots
         */
        public final int secondaryColor;
        public final StatBase killEntityStat;
        public final StatBase entityKilledByStat;

        public EntityEggInfo(ResourceLocation idIn, int primaryColorIn, int secondaryColorIn) {
            this.spawnedID = idIn;
            this.primaryColor = primaryColorIn;
            this.secondaryColor = secondaryColorIn;
            this.killEntityStat = StatList.getStatKillEntity(this);
            this.entityKilledByStat = StatList.getStatEntityKilledBy(this);
        }

        // Forge start
        public EntityEggInfo(ResourceLocation id, int primaryColor, int secondaryColor, StatBase killEntityStatistic,
                             StatBase entityKilledByStatistic) {
            this.spawnedID = id;
            this.primaryColor = primaryColor;
            this.secondaryColor = secondaryColor;
            this.killEntityStat = killEntityStatistic;
            this.entityKilledByStat = entityKilledByStatistic;
        }
        // Forge end
    }
}
