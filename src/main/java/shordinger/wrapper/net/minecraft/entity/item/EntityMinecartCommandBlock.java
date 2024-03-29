package shordinger.wrapper.net.minecraft.entity.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.tileentity.CommandBlockBaseLogic;
import shordinger.wrapper.net.minecraft.tileentity.TileEntity;
import shordinger.wrapper.net.minecraft.tileentity.TileEntityCommandBlock;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.util.datafix.FixTypes;
import shordinger.wrapper.net.minecraft.util.datafix.IDataFixer;
import shordinger.wrapper.net.minecraft.util.datafix.IDataWalker;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;
import shordinger.wrapper.net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {

    private static final DataParameter<String> COMMAND = EntityDataManager
        .<String>createKey(EntityMinecartCommandBlock.class, DataSerializers.STRING);
    private static final DataParameter<ITextComponent> LAST_OUTPUT = EntityDataManager
        .<ITextComponent>createKey(EntityMinecartCommandBlock.class, DataSerializers.TEXT_COMPONENT);
    private final CommandBlockBaseLogic commandBlockLogic = new CommandBlockBaseLogic() {

        public void updateCommand() {
            EntityMinecartCommandBlock.this.getDataManager()
                .set(EntityMinecartCommandBlock.COMMAND, this.getCommand());
            EntityMinecartCommandBlock.this.getDataManager()
                .set(EntityMinecartCommandBlock.LAST_OUTPUT, this.getLastOutput());
        }

        /**
         * Currently this returns 0 for the traditional command block, and 1 for the minecart command block
         */
        @SideOnly(Side.CLIENT)
        public int getCommandBlockType() {
            return 1;
        }

        /**
         * Fills in information about the command block for the packet. entityId for the minecart version, and X/Y/Z for
         * the traditional version
         */
        @SideOnly(Side.CLIENT)
        public void fillInInfo(ByteBuf buf) {
            buf.writeInt(EntityMinecartCommandBlock.this.getEntityId());
        }

        /**
         * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the coordinates 0, 0, 0
         */
        public BlockPos getPosition() {
            return new BlockPos(
                EntityMinecartCommandBlock.this.posX,
                EntityMinecartCommandBlock.this.posY + 0.5D,
                EntityMinecartCommandBlock.this.posZ);
        }

        /**
         * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
         * 0.0D, 0.0D, 0.0D
         */
        public Vec3d getPositionVector() {
            return new Vec3d(
                EntityMinecartCommandBlock.this.posX,
                EntityMinecartCommandBlock.this.posY,
                EntityMinecartCommandBlock.this.posZ);
        }

        /**
         * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the overworld
         */
        public World getEntityWorld() {
            return EntityMinecartCommandBlock.this.world;
        }

        /**
         * Returns the entity associated with the command sender. MAY BE NULL!
         */
        public Entity getCommandSenderEntity() {
            return EntityMinecartCommandBlock.this;
        }

        /**
         * Get the Minecraft server instance
         */
        public MinecraftServer getServer() {
            return EntityMinecartCommandBlock.this.world.getMinecraftServer();
        }
    };
    /**
     * Cooldown before command block logic runs again in ticks
     */
    private int activatorRailCooldown;

    public EntityMinecartCommandBlock(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartCommandBlock(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public static void registerFixesMinecartCommand(DataFixer fixer) {
        EntityMinecart.registerFixesMinecart(fixer, EntityMinecartCommandBlock.class);
        fixer.registerWalker(FixTypes.ENTITY, new IDataWalker() {

            public NBTTagCompound process(IDataFixer fixer, NBTTagCompound compound, int versionIn) {
                if (TileEntity.getKey(TileEntityCommandBlock.class)
                    .equals(new ResourceLocation(compound.getString("id")))) {
                    compound.setString("id", "Control");
                    fixer.process(FixTypes.BLOCK_ENTITY, compound, versionIn);
                    compound.setString("id", "MinecartCommandBlock");
                }

                return compound;
            }
        });
    }

    protected void entityInit() {
        super.entityInit();
        this.getDataManager()
            .register(COMMAND, "");
        this.getDataManager()
            .register(LAST_OUTPUT, new TextComponentString(""));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.commandBlockLogic.readDataFromNBT(compound);
        this.getDataManager()
            .set(
                COMMAND,
                this.getCommandBlockLogic()
                    .getCommand());
        this.getDataManager()
            .set(
                LAST_OUTPUT,
                this.getCommandBlockLogic()
                    .getLastOutput());
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        this.commandBlockLogic.writeToNBT(compound);
    }

    public EntityMinecart.Type getType() {
        return EntityMinecart.Type.COMMAND_BLOCK;
    }

    public IBlockState getDefaultDisplayTile() {
        return Blocks.COMMAND_BLOCK.getDefaultState();
    }

    public CommandBlockBaseLogic getCommandBlockLogic() {
        return this.commandBlockLogic;
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        if (receivingPower && this.ticksExisted - this.activatorRailCooldown >= 4) {
            this.getCommandBlockLogic()
                .trigger(this.world);
            this.activatorRailCooldown = this.ticksExisted;
        }
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (super.processInitialInteract(player, hand)) return true;
        this.commandBlockLogic.tryOpenEditCommandBlock(player);
        return false;
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if (LAST_OUTPUT.equals(key)) {
            try {
                this.commandBlockLogic.setLastOutput(
                    (ITextComponent) this.getDataManager()
                        .get(LAST_OUTPUT));
            } catch (Throwable var3) {
                ;
            }
        } else if (COMMAND.equals(key)) {
            this.commandBlockLogic.setCommand(
                (String) this.getDataManager()
                    .get(COMMAND));
        }
    }

    public boolean ignoreItemEntityData() {
        return true;
    }
}
