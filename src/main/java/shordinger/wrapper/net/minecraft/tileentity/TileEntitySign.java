package shordinger.wrapper.net.minecraft.tileentity;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.command.CommandException;
import shordinger.wrapper.net.minecraft.command.CommandResultStats;
import shordinger.wrapper.net.minecraft.command.ICommandSender;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.wrapper.net.minecraft.network.play.server.SPacketUpdateTileEntity;
import shordinger.wrapper.net.minecraft.server.MinecraftServer;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.Style;
import shordinger.wrapper.net.minecraft.util.text.TextComponentString;
import shordinger.wrapper.net.minecraft.util.text.TextComponentUtils;
import shordinger.wrapper.net.minecraft.util.text.event.ClickEvent;
import shordinger.wrapper.net.minecraft.world.World;

public class TileEntitySign extends TileEntity {

    public final ITextComponent[] signText = new ITextComponent[]{new TextComponentString(""),
        new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};
    /**
     * The index of the line currently being edited. Only used on client side, but defined on both. Note this is only
     * really used when the > < are going to be visible.
     */
    public int lineBeingEdited = -1;
    private boolean isEditable = true;
    private EntityPlayer player;
    private final CommandResultStats stats = new CommandResultStats();

    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        for (int i = 0; i < 4; ++i) {
            String s = ITextComponent.Serializer.componentToJson(this.signText[i]);
            compound.setString("Text" + (i + 1), s);
        }

        this.stats.writeStatsToNBT(compound);
        return compound;
    }

    protected void setWorldCreate(World worldIn) {
        this.setWorld(worldIn);
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.isEditable = false;
        super.readFromNBT(compound);
        ICommandSender icommandsender = new ICommandSender() {

            /**
             * Get the name of this object. For players this returns their username
             */
            public String getName() {
                return "Sign";
            }

            /**
             * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
             */
            public boolean canUseCommand(int permLevel, String commandName) {
                return permLevel <= 2; // Forge: Fixes MC-75630 - Exploit with signs and command blocks
            }

            /**
             * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the coordinates 0, 0, 0
             */
            public BlockPos getPosition() {
                return TileEntitySign.this.pos;
            }

            /**
             * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return 0.0D, 0.0D, 0.0D
             */
            public Vec3d getPositionVector() {
                return new Vec3d(
                    (double) TileEntitySign.this.pos.getX() + 0.5D,
                    (double) TileEntitySign.this.pos.getY() + 0.5D,
                    (double) TileEntitySign.this.pos.getZ() + 0.5D);
            }

            /**
             * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the overworld
             */
            public World getEntityWorld() {
                return TileEntitySign.this.world;
            }

            /**
             * Get the Minecraft server instance
             */
            public MinecraftServer getServer() {
                return TileEntitySign.this.world.getMinecraftServer();
            }
        };

        for (int i = 0; i < 4; ++i) {
            String s = compound.getString("Text" + (i + 1));
            ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s);

            try {
                this.signText[i] = TextComponentUtils.processComponent(icommandsender, itextcomponent, (Entity) null);
            } catch (CommandException var7) {
                this.signText[i] = itextcomponent;
            }
        }

        this.stats.readStatsFromNBT(compound);
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
     */
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public boolean onlyOpsCanSetNbt() {
        return true;
    }

    public boolean getIsEditable() {
        return this.isEditable;
    }

    /**
     * Sets the sign's isEditable flag to the specified parameter.
     */
    @SideOnly(Side.CLIENT)
    public void setEditable(boolean isEditableIn) {
        this.isEditable = isEditableIn;

        if (!isEditableIn) {
            this.player = null;
        }
    }

    public void setPlayer(EntityPlayer playerIn) {
        this.player = playerIn;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public boolean executeCommand(final EntityPlayer playerIn) {
        ICommandSender icommandsender = new ICommandSender() {

            /**
             * Get the name of this object. For players this returns their username
             */
            public String getName() {
                return playerIn.getName();
            }

            /**
             * Get the formatted ChatComponent that will be used for the sender's username in chat
             */
            public ITextComponent getDisplayName() {
                return playerIn.getDisplayName();
            }

            /**
             * Send a chat message to the CommandSender
             */
            public void sendMessage(ITextComponent component) {
            }

            /**
             * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
             */
            public boolean canUseCommand(int permLevel, String commandName) {
                return permLevel <= 2;
            }

            /**
             * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the coordinates 0, 0, 0
             */
            public BlockPos getPosition() {
                return TileEntitySign.this.pos;
            }

            /**
             * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return 0.0D, 0.0D, 0.0D
             */
            public Vec3d getPositionVector() {
                return new Vec3d(
                    (double) TileEntitySign.this.pos.getX() + 0.5D,
                    (double) TileEntitySign.this.pos.getY() + 0.5D,
                    (double) TileEntitySign.this.pos.getZ() + 0.5D);
            }

            /**
             * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the overworld
             */
            public World getEntityWorld() {
                return playerIn.getEntityWorld();
            }

            /**
             * Returns the entity associated with the command sender. MAY BE NULL!
             */
            public Entity getCommandSenderEntity() {
                return playerIn;
            }

            /**
             * Returns true if the command sender should be sent feedback about executed commands
             */
            public boolean sendCommandFeedback() {
                return false;
            }

            public void setCommandStat(CommandResultStats.Type type, int amount) {
                if (TileEntitySign.this.world != null && !TileEntitySign.this.world.isRemote) {
                    TileEntitySign.this.stats
                        .setCommandStatForSender(TileEntitySign.this.world.getMinecraftServer(), this, type, amount);
                }
            }

            /**
             * Get the Minecraft server instance
             */
            public MinecraftServer getServer() {
                return playerIn.getServer();
            }
        };

        for (ITextComponent itextcomponent : this.signText) {
            Style style = itextcomponent == null ? null : itextcomponent.getStyle();

            if (style != null && style.getClickEvent() != null) {
                ClickEvent clickevent = style.getClickEvent();

                if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    playerIn.getServer()
                        .getCommandManager()
                        .executeCommand(icommandsender, clickevent.getValue());
                }
            }
        }

        return true;
    }

    public CommandResultStats getStats() {
        return this.stats;
    }
}
