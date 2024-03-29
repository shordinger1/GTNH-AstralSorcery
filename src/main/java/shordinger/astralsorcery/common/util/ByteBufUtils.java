/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.NBTTagCompound;
import shordinger.astralsorcery.common.util.data.Vector3;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.nbt.CompressedStreamTools;
import shordinger.wrapper.net.minecraft.network.PacketBuffer;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ByteBufUtils
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:13
 */
public class ByteBufUtils {

    @Nullable
    public static <T> T readOptional(ByteBuf buf, Function<ByteBuf, T> readFct) {
        if (buf.readBoolean()) {
            return readFct.apply(buf);
        }
        return null;
    }

    public static <T> void writeOptional(ByteBuf buf, @Nullable T object, BiConsumer<ByteBuf, T> applyFct) {
        buf.writeBoolean(object != null);
        if (object != null) {
            applyFct.accept(buf, object);
        }
    }

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeString(PacketBuffer buf, String toWrite) {
        byte[] str = toWrite.getBytes(Charset.forName("UTF-8"));
        buf.writeInt(str.length);
        buf.writeBytes(str);
    }

    public static void writeString(ByteBuf buf, String toWrite) {
        byte[] str = toWrite.getBytes(Charset.forName("UTF-8"));
        buf.writeInt(str.length);
        buf.writeBytes(str);
    }

    public static String readString(PacketBuffer buf) {
        int length = buf.readInt();
        byte[] strBytes = new byte[length];
        buf.readBytes(strBytes, 0, length);
        return new String(strBytes, Charset.forName("UTF-8"));
    }

    public static String readString(ByteBuf buf) {
        int length = buf.readInt();
        byte[] strBytes = new byte[length];
        buf.readBytes(strBytes, 0, length);
        return new String(strBytes, Charset.forName("UTF-8"));
    }

    public static void writeResourceLocation(ByteBuf buf, ResourceLocation key) {
        writeString(buf, key.toString());
    }

    public static ResourceLocation readResourceLocation(ByteBuf buf) {
        return new ResourceLocation(readString(buf));
    }

    public static <T extends Enum<T>> void writeEnumValue(ByteBuf buf, T value) {
        buf.writeInt(value.ordinal());
    }

    public static <T extends Enum<T>> T readEnumValue(ByteBuf buf, Class<T> enumClazz) {
        if (!enumClazz.isEnum()) {
            throw new IllegalArgumentException("Passed class is not an enum!");
        }
        return enumClazz.getEnumConstants()[buf.readInt()];
    }

    public static void writePos(ByteBuf buf, BlockPos pos) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static BlockPos readPos(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        return new BlockPos(x, y, z);
    }

    public static void writeVector(ByteBuf buf, Vector3 vec) {
        buf.writeDouble(vec.getX());
        buf.writeDouble(vec.getY());
        buf.writeDouble(vec.getZ());
    }

    public static Vector3 readVector(ByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vector3(x, y, z);
    }

    public static void writeItemStack(ByteBuf byteBuf, @Nonnull ItemStack stack) {
        boolean defined = !stack.isEmpty();
        byteBuf.writeBoolean(defined);
        if (defined) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            writeNBTTag(byteBuf, tag);
        }
    }

    @Nonnull
    public static ItemStack readItemStack(ByteBuf byteBuf) {
        boolean defined = byteBuf.readBoolean();
        if (defined) {
            return new ItemStack(readNBTTag(byteBuf));
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static void writeFluidStack(ByteBuf byteBuf, @Nullable FluidStack stack) {
        boolean defined = stack != null;
        byteBuf.writeBoolean(defined);
        if (defined) {
            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);
            writeNBTTag(byteBuf, tag);
        }
    }

    @Nullable
    public static FluidStack readFluidStack(ByteBuf byteBuf) {
        boolean defined = byteBuf.readBoolean();
        if (defined) {
            return FluidStack.loadFluidStackFromNBT(readNBTTag(byteBuf));
        } else {
            return null;
        }
    }

    public static void writeNBTTag(ByteBuf byteBuf, @Nonnull NBTTagCompound tag) {
        try (DataOutputStream dos = new DataOutputStream(new ByteBufOutputStream(byteBuf))) {
            CompressedStreamTools.write(tag, dos);
        } catch (Exception exc) {}
    }

    @Nonnull
    public static NBTTagCompound readNBTTag(ByteBuf byteBuf) {
        try (DataInputStream dis = new DataInputStream(new ByteBufInputStream(byteBuf))) {
            return CompressedStreamTools.read(dis);
        } catch (Exception exc) {}
        throw new IllegalStateException("Could not load NBT Tag from incoming byte buffer!");
    }

}
