package shordinger.wrapper.net.minecraft.init;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shordinger.wrapper.net.minecraft.advancements.AdvancementManager;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.BlockDispenser;
import shordinger.wrapper.net.minecraft.block.BlockFire;
import shordinger.wrapper.net.minecraft.block.BlockLiquid;
import shordinger.wrapper.net.minecraft.block.BlockPumpkin;
import shordinger.wrapper.net.minecraft.block.BlockShulkerBox;
import shordinger.wrapper.net.minecraft.block.BlockSkull;
import shordinger.wrapper.net.minecraft.block.BlockTNT;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import shordinger.wrapper.net.minecraft.dispenser.BehaviorProjectileDispense;
import shordinger.wrapper.net.minecraft.dispenser.IBehaviorDispenseItem;
import shordinger.wrapper.net.minecraft.dispenser.IBlockSource;
import shordinger.wrapper.net.minecraft.dispenser.IPosition;
import shordinger.wrapper.net.minecraft.enchantment.Enchantment;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityList;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.IProjectile;
import shordinger.wrapper.net.minecraft.entity.item.EntityBoat;
import shordinger.wrapper.net.minecraft.entity.item.EntityExpBottle;
import shordinger.wrapper.net.minecraft.entity.item.EntityFireworkRocket;
import shordinger.wrapper.net.minecraft.entity.item.EntityTNTPrimed;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayerMP;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityEgg;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityPotion;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySmallFireball;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySnowball;
import shordinger.wrapper.net.minecraft.entity.projectile.EntitySpectralArrow;
import shordinger.wrapper.net.minecraft.entity.projectile.EntityTippedArrow;
import shordinger.wrapper.net.minecraft.item.EnumDyeColor;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.item.ItemArmor;
import shordinger.wrapper.net.minecraft.item.ItemBucket;
import shordinger.wrapper.net.minecraft.item.ItemDye;
import shordinger.wrapper.net.minecraft.item.ItemMonsterPlacer;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.item.crafting.CraftingManager;
import shordinger.wrapper.net.minecraft.nbt.NBTUtil;
import shordinger.wrapper.net.minecraft.potion.Potion;
import shordinger.wrapper.net.minecraft.potion.PotionHelper;
import shordinger.wrapper.net.minecraft.potion.PotionType;
import shordinger.wrapper.net.minecraft.server.DebugLoggingPrintStream;
import shordinger.wrapper.net.minecraft.stats.StatList;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityDispenser;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityShulkerBox;
import shordinger.wrapper.net.minecraft.tileentity.TileEntitySkull;
import shordinger.wrapper.net.minecraft.util.EnumFacing;
import shordinger.wrapper.net.minecraft.util.LoggingPrintStream;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.SoundEvent;
import shordinger.wrapper.net.minecraft.util.StringUtils;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.biome.Biome;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

import java.io.File;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;

public class Bootstrap {

    public static final PrintStream SYSOUT = System.out;
    /**
     * Whether the blocks, items, etc have already been registered
     */
    private static boolean alreadyRegistered;
    public static boolean hasErrored;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Is Bootstrap registration already done?
     */
    public static boolean isRegistered() {
        return alreadyRegistered;
    }

    static void registerDispenserBehaviors() {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.ARROW, new BehaviorProjectileDispense() {

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(
                    worldIn,
                    position.getX(),
                    position.getY(),
                    position.getZ());
                entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.TIPPED_ARROW, new BehaviorProjectileDispense() {

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(
                    worldIn,
                    position.getX(),
                    position.getY(),
                    position.getZ());
                entitytippedarrow.setPotionEffect(stackIn);
                entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SPECTRAL_ARROW, new BehaviorProjectileDispense() {

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                EntityArrow entityarrow = new EntitySpectralArrow(
                    worldIn,
                    position.getX(),
                    position.getY(),
                    position.getZ());
                entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
                return entityarrow;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.EGG, new BehaviorProjectileDispense() {

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SNOWBALL, new BehaviorProjectileDispense() {

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.EXPERIENCE_BOTTLE, new BehaviorProjectileDispense() {

            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
            }

            protected float getProjectileInaccuracy() {
                return super.getProjectileInaccuracy() * 0.5F;
            }

            protected float getProjectileVelocity() {
                return super.getProjectileVelocity() * 1.25F;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SPLASH_POTION, new IBehaviorDispenseItem() {

            /**
             * Dispenses the specified ItemStack from a dispenser.
             */
            public ItemStack dispense(IBlockSource source, final ItemStack stack) {
                return (new BehaviorProjectileDispense() {

                    /**
                     * Return the projectile entity spawned by this dispense behavior.
                     */
                    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                        return new EntityPotion(
                            worldIn,
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            stack.copy());
                    }

                    protected float getProjectileInaccuracy() {
                        return super.getProjectileInaccuracy() * 0.5F;
                    }

                    protected float getProjectileVelocity() {
                        return super.getProjectileVelocity() * 1.25F;
                    }
                }).dispense(source, stack);
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.LINGERING_POTION, new IBehaviorDispenseItem() {

            /**
             * Dispenses the specified ItemStack from a dispenser.
             */
            public ItemStack dispense(IBlockSource source, final ItemStack stack) {
                return (new BehaviorProjectileDispense() {

                    /**
                     * Return the projectile entity spawned by this dispense behavior.
                     */
                    protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                        return new EntityPotion(
                            worldIn,
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            stack.copy());
                    }

                    protected float getProjectileInaccuracy() {
                        return super.getProjectileInaccuracy() * 0.5F;
                    }

                    protected float getProjectileVelocity() {
                        return super.getProjectileVelocity() * 1.25F;
                    }
                }).dispense(source, stack);
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SPAWN_EGG, new BehaviorDefaultDispenseItem() {

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing enumfacing = (EnumFacing) source.getBlockState()
                    .getValue(BlockDispenser.FACING);
                double d0 = source.getX() + (double) enumfacing.getFrontOffsetX();
                double d1 = (double) ((float) (source.getBlockPos()
                    .getY() + enumfacing.getFrontOffsetY()) + 0.2F);
                double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ();
                Entity entity = ItemMonsterPlacer
                    .spawnCreature(source.getWorld(), ItemMonsterPlacer.getNamedIdFrom(stack), d0, d1, d2);

                if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

                ItemMonsterPlacer.applyItemEntityDataToEntity(source.getWorld(), (EntityPlayer) null, stack, entity);
                stack.shrink(1);
                return stack;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.FIREWORKS, new BehaviorDefaultDispenseItem() {

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing enumfacing = (EnumFacing) source.getBlockState()
                    .getValue(BlockDispenser.FACING);
                double d0 = source.getX() + (double) enumfacing.getFrontOffsetX();
                double d1 = (double) ((float) source.getBlockPos()
                    .getY() + 0.2F);
                double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ();
                EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(
                    source.getWorld(),
                    d0,
                    d1,
                    d2,
                    stack);
                source.getWorld()
                    .spawnEntity(entityfireworkrocket);
                stack.shrink(1);
                return stack;
            }

            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source) {
                source.getWorld()
                    .playEvent(1004, source.getBlockPos(), 0);
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.FIRE_CHARGE, new BehaviorDefaultDispenseItem() {

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing enumfacing = (EnumFacing) source.getBlockState()
                    .getValue(BlockDispenser.FACING);
                IPosition iposition = BlockDispenser.getDispensePosition(source);
                double d0 = iposition.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 0.3F);
                double d1 = iposition.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 0.3F);
                double d2 = iposition.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 0.3F);
                World world = source.getWorld();
                Random random = world.rand;
                double d3 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetX();
                double d4 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetY();
                double d5 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetZ();
                world.spawnEntity(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
                stack.shrink(1);
                return stack;
            }

            /**
             * Play the dispense sound from the specified block.
             */
            protected void playDispenseSound(IBlockSource source) {
                source.getWorld()
                    .playEvent(1018, source.getBlockPos(), 0);
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.OAK));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.SPRUCE_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.SPRUCE));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.BIRCH_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.BIRCH));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.JUNGLE_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.JUNGLE));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.DARK_OAK_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.DARK_OAK));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.ACACIA_BOAT, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.ACACIA));
        IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {

            private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                ItemBucket itembucket = (ItemBucket) stack.getItem();
                BlockPos blockpos = source.getBlockPos()
                    .offset(
                        (EnumFacing) source.getBlockState()
                            .getValue(BlockDispenser.FACING));
                return itembucket.tryPlaceContainedLiquid((EntityPlayer) null, source.getWorld(), blockpos)
                    ? new ItemStack(Items.BUCKET)
                    : this.dispenseBehavior.dispense(source, stack);
            }
        };
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.LAVA_BUCKET, ibehaviordispenseitem);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.WATER_BUCKET, ibehaviordispenseitem);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.MILK_BUCKET, net.minecraftforge.fluids.DispenseFluidContainer.getInstance());
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.BUCKET, net.minecraftforge.fluids.DispenseFluidContainer.getInstance());
        if (false) BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.BUCKET, new BehaviorDefaultDispenseItem() {

            private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                World world = source.getWorld();
                BlockPos blockpos = source.getBlockPos()
                    .offset(
                        (EnumFacing) source.getBlockState()
                            .getValue(BlockDispenser.FACING));
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                Material material = iblockstate.getMaterial();
                Item item;

                if (Material.WATER.equals(material) && block instanceof BlockLiquid
                    && ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0) {
                    item = Items.WATER_BUCKET;
                } else {
                    if (!Material.LAVA.equals(material) || !(block instanceof BlockLiquid)
                        || ((Integer) iblockstate.getValue(BlockLiquid.LEVEL)).intValue() != 0) {
                        return super.dispenseStack(source, stack);
                    }

                    item = Items.LAVA_BUCKET;
                }

                world.setBlockToAir(blockpos);
                stack.shrink(1);

                if (stack.isEmpty()) {
                    return new ItemStack(item);
                } else {
                    if (((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(new ItemStack(item)) < 0) {
                        this.dispenseBehavior.dispense(source, new ItemStack(item));
                    }

                    return stack;
                }
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Items.FLINT_AND_STEEL, new Bootstrap.BehaviorDispenseOptional() {

                /**
                 * Dispense the specified stack, play the dispense sound and spawn particles.
                 */
                protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                    World world = source.getWorld();
                    this.successful = true;
                    BlockPos blockpos = source.getBlockPos()
                        .offset(
                            (EnumFacing) source.getBlockState()
                                .getValue(BlockDispenser.FACING));

                    if (world.isAirBlock(blockpos)) {
                        world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());

                        if (stack.attemptDamageItem(1, world.rand, (EntityPlayerMP) null)) {
                            stack.setCount(0);
                        }
                    } else if (world.getBlockState(blockpos)
                        .getBlock() == Blocks.TNT) {
                        Blocks.TNT.onBlockDestroyedByPlayer(
                            world,
                            blockpos,
                            Blocks.TNT.getDefaultState()
                                .withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
                        world.setBlockToAir(blockpos);
                    } else {
                        this.successful = false;
                    }

                    return stack;
                }
            });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.DYE, new Bootstrap.BehaviorDispenseOptional() {

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                this.successful = true;

                if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
                    World world = source.getWorld();
                    BlockPos blockpos = source.getBlockPos()
                        .offset(
                            (EnumFacing) source.getBlockState()
                                .getValue(BlockDispenser.FACING));

                    if (ItemDye.applyBonemeal(stack, world, blockpos)) {
                        if (!world.isRemote) {
                            world.playEvent(2005, blockpos, 0);
                        }
                    } else {
                        this.successful = false;
                    }

                    return stack;
                } else {
                    return super.dispenseStack(source, stack);
                }
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Item.getItemFromBlock(Blocks.TNT), new BehaviorDefaultDispenseItem() {

                /**
                 * Dispense the specified stack, play the dispense sound and spawn particles.
                 */
                protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                    World world = source.getWorld();
                    BlockPos blockpos = source.getBlockPos()
                        .offset(
                            (EnumFacing) source.getBlockState()
                                .getValue(BlockDispenser.FACING));
                    EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(
                        world,
                        (double) blockpos.getX() + 0.5D,
                        (double) blockpos.getY(),
                        (double) blockpos.getZ() + 0.5D,
                        (EntityLivingBase) null);
                    world.spawnEntity(entitytntprimed);
                    world.playSound(
                        (EntityPlayer) null,
                        entitytntprimed.posX,
                        entitytntprimed.posY,
                        entitytntprimed.posZ,
                        SoundEvents.ENTITY_TNT_PRIMED,
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F);
                    stack.shrink(1);
                    return stack;
                }
            });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.SKULL, new Bootstrap.BehaviorDispenseOptional() {

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                World world = source.getWorld();
                EnumFacing enumfacing = (EnumFacing) source.getBlockState()
                    .getValue(BlockDispenser.FACING);
                BlockPos blockpos = source.getBlockPos()
                    .offset(enumfacing);
                BlockSkull blockskull = Blocks.SKULL;
                this.successful = true;

                if (world.isAirBlock(blockpos) && blockskull.canDispenserPlace(world, blockpos, stack)) {
                    if (!world.isRemote) {
                        world.setBlockState(
                            blockpos,
                            blockskull.getDefaultState()
                                .withProperty(BlockSkull.FACING, EnumFacing.UP),
                            3);
                        TileEntity tileentity = world.getTileEntity(blockpos);

                        if (tileentity instanceof TileEntitySkull) {
                            if (stack.getMetadata() == 3) {
                                GameProfile gameprofile = null;

                                if (stack.hasTagCompound()) {
                                    NBTTagCompound nbttagcompound = stack.getTagCompound();

                                    if (nbttagcompound.hasKey("SkullOwner", 10)) {
                                        gameprofile = NBTUtil
                                            .readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                                    } else if (nbttagcompound.hasKey("SkullOwner", 8)) {
                                        String s = nbttagcompound.getString("SkullOwner");

                                        if (!StringUtils.isNullOrEmpty(s)) {
                                            gameprofile = new GameProfile((UUID) null, s);
                                        }
                                    }
                                }

                                ((TileEntitySkull) tileentity).setPlayerProfile(gameprofile);
                            } else {
                                ((TileEntitySkull) tileentity).setType(stack.getMetadata());
                            }

                            ((TileEntitySkull) tileentity).setSkullRotation(
                                enumfacing.getOpposite()
                                    .getHorizontalIndex() * 4);
                            Blocks.SKULL.checkWitherSpawn(world, blockpos, (TileEntitySkull) tileentity);
                        }

                        stack.shrink(1);
                    }
                } else if (ItemArmor.dispenseArmor(source, stack)
                    .isEmpty()) {
                    this.successful = false;
                }

                return stack;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY
            .putObject(Item.getItemFromBlock(Blocks.PUMPKIN), new Bootstrap.BehaviorDispenseOptional() {

                /**
                 * Dispense the specified stack, play the dispense sound and spawn particles.
                 */
                protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                    World world = source.getWorld();
                    BlockPos blockpos = source.getBlockPos()
                        .offset(
                            (EnumFacing) source.getBlockState()
                                .getValue(BlockDispenser.FACING));
                    BlockPumpkin blockpumpkin = (BlockPumpkin) Blocks.PUMPKIN;
                    this.successful = true;

                    if (world.isAirBlock(blockpos) && blockpumpkin.canDispenserPlace(world, blockpos)) {
                        if (!world.isRemote) {
                            world.setBlockState(blockpos, blockpumpkin.getDefaultState(), 3);
                        }

                        stack.shrink(1);
                    } else {
                        ItemStack itemstack = ItemArmor.dispenseArmor(source, stack);

                        if (itemstack.isEmpty()) {
                            this.successful = false;
                        }
                    }

                    return stack;
                }
            });

        for (EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
                Item.getItemFromBlock(BlockShulkerBox.getBlockByColor(enumdyecolor)),
                new Bootstrap.BehaviorDispenseShulkerBox());
        }
    }

    /**
     * Registers blocks, items, stats, etc.
     */
//    public static void register() {
//        if (!alreadyRegistered) {
//            alreadyRegistered = true;
//            if (false) // skip redirectOutputToLog, Forge already redirects stdout and stderr output to log so that they
//                // print with more context
//                redirectOutputToLog();
//            SoundEvent.registerSounds();
//            Block.registerBlocks();
//            BlockFire.init();
//            Potion.registerPotions();
//            Enchantment.registerEnchantments();
//            Item.registerItems();
//            PotionType.registerPotionTypes();
//            PotionHelper.init();
//            EntityList.init();
//            Biome.registerBiomes();
//            registerDispenserBehaviors();
//
//            if (!CraftingManager.init()) {
//                hasErrored = true;
//                LOGGER.error("Errors with built-in recipes!");
//            }
//
//            StatList.init();
//
//            if (LOGGER.isDebugEnabled()) {
//                if ((new AdvancementManager((File) null)).hasErrored()) {
//                    hasErrored = true;
//                    LOGGER.error("Errors with built-in advancements!");
//                }
//
//                if (!LootTableList.test()) {
//                    hasErrored = true;
//                    LOGGER.error("Errors with built-in loot tables");
//                }
//            }
//
//            net.minecraftforge.registries.GameData.vanillaSnapshot();
//        }
//    }

    /**
     * redirect standard streams to logger
     */
    private static void redirectOutputToLog() {
        if (LOGGER.isDebugEnabled()) {
            System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
            System.setOut(new DebugLoggingPrintStream("STDOUT", SYSOUT));
        } else {
            System.setErr(new LoggingPrintStream("STDERR", System.err));
            System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void printToSYSOUT(String message) {
        SYSOUT.println(message);
    }

    public static class BehaviorDispenseBoat extends BehaviorDefaultDispenseItem {

        private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();
        private final EntityBoat.Type boatType;

        public BehaviorDispenseBoat(EntityBoat.Type boatTypeIn) {
            this.boatType = boatTypeIn;
        }

        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing enumfacing = (EnumFacing) source.getBlockState()
                .getValue(BlockDispenser.FACING);
            World world = source.getWorld();
            double d0 = source.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 1.125F);
            double d1 = source.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 1.125F);
            double d2 = source.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 1.125F);
            BlockPos blockpos = source.getBlockPos()
                .offset(enumfacing);
            Material material = world.getBlockState(blockpos)
                .getMaterial();
            double d3;

            if (Material.WATER.equals(material)) {
                d3 = 1.0D;
            } else {
                if (!Material.AIR.equals(material) || !Material.WATER.equals(
                    world.getBlockState(blockpos.down())
                        .getMaterial())) {
                    return this.dispenseBehavior.dispense(source, stack);
                }

                d3 = 0.0D;
            }

            EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
            entityboat.setBoatType(this.boatType);
            entityboat.rotationYaw = enumfacing.getHorizontalAngle();
            world.spawnEntity(entityboat);
            stack.shrink(1);
            return stack;
        }

        /**
         * Play the dispense sound from the specified block.
         */
        protected void playDispenseSound(IBlockSource source) {
            source.getWorld()
                .playEvent(1000, source.getBlockPos(), 0);
        }
    }

    public abstract static class BehaviorDispenseOptional extends BehaviorDefaultDispenseItem {

        protected boolean successful = true;

        /**
         * Play the dispense sound from the specified block.
         */
        protected void playDispenseSound(IBlockSource source) {
            source.getWorld()
                .playEvent(this.successful ? 1000 : 1001, source.getBlockPos(), 0);
        }
    }

    static class BehaviorDispenseShulkerBox extends Bootstrap.BehaviorDispenseOptional {

        private BehaviorDispenseShulkerBox() {
        }

        /**
         * Dispense the specified stack, play the dispense sound and spawn particles.
         */
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Block block = Block.getBlockFromItem(stack.getItem());
            World world = source.getWorld();
            EnumFacing enumfacing = (EnumFacing) source.getBlockState()
                .getValue(BlockDispenser.FACING);
            BlockPos blockpos = source.getBlockPos()
                .offset(enumfacing);
            this.successful = world.mayPlace(block, blockpos, false, EnumFacing.DOWN, (Entity) null);

            if (this.successful) {
                EnumFacing enumfacing1 = world.isAirBlock(blockpos.down()) ? enumfacing : EnumFacing.UP;
                IBlockState iblockstate = block.getDefaultState()
                    .withProperty(BlockShulkerBox.FACING, enumfacing1);
                world.setBlockState(blockpos, iblockstate);
                TileEntity tileentity = world.getTileEntity(blockpos);
                ItemStack itemstack = stack.splitStack(1);

                if (itemstack.hasTagCompound()) {
                    ((TileEntityShulkerBox) tileentity).loadFromNbt(
                        itemstack.getTagCompound()
                            .getCompoundTag("BlockEntityTag"));
                }

                if (itemstack.hasDisplayName()) {
                    ((TileEntityShulkerBox) tileentity).setCustomName(itemstack.getDisplayName());
                }

                world.updateComparatorOutputLevel(blockpos, iblockstate.getBlock());
            }

            return stack;
        }
    }
}
