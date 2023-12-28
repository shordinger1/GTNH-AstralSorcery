package shordinger.astralsorcery.migration;

import java.io.DataInput;
import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ReportedException;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;

public class PacketBufferWrapper {

    public static PacketBuffer writeCompoundTag(PacketBuffer pb, @Nullable NBTTagCompound nbt) {
        if (nbt == null) {
            pb.writeByte(0);
        } else {
            try {
                CompressedStreamTools.write(nbt, new ByteBufOutputStream(pb));
            } catch (IOException var3) {
                throw new EncoderException(var3);
            }
        }

        return pb;
    }

    @Nullable
    public static NBTTagCompound readCompoundTag(PacketBuffer pb) throws IOException {
        int i = pb.readerIndex();
        byte b0 = pb.readByte();
        if (b0 == 0) {
            return null;
        } else {
            pb.readerIndex(i);

            try {
                return read(new ByteBufInputStream(pb), new NBTSizeTracker(2097152L));
            } catch (IOException var4) {
                throw new EncoderException(var4);
            }
        }
    }

    public static NBTTagCompound read(DataInput input, NBTSizeTracker accounter) throws IOException {
        NBTBase nbtbase = read(input, 0, accounter);
        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    private static NBTBase read(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
        byte b0 = input.readByte();
        accounter.func_152450_a(8L);
        if (b0 == 0) {
            return new NBTTagEnd();
        } else {
            NBTSizeTracker.readUTF(accounter, input.readUTF());
            accounter.func_152450_a(32L);
            NBTBase nbtbase = NBTBase.func_150284_a(b0);

            try {
                nbtbase.read(input, depth, accounter);
                return nbtbase;
            } catch (IOException var8) {
                CrashReport crashreport = CrashReport.makeCrashReport(var8, "Loading NBT data");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
                crashreportcategory.addCrashSection("Tag type", b0);
                throw new ReportedException(crashreport);
            }
        }

    }

    protected static NBTBase createNewByType(byte id) {
        return switch (id) {
            case 0 -> new NBTTagEnd();
            case 1 -> new NBTTagByte();
            case 2 -> new NBTTagShort();
            case 3 -> new NBTTagInt();
            case 4 -> new NBTTagLong();
            case 5 -> new NBTTagFloat();
            case 6 -> new NBTTagDouble();
            case 7 -> new NBTTagByteArray();
            case 8 -> new NBTTagString();
            case 9 -> new NBTTagList();
            case 10 -> new NBTTagCompound();
            case 11 -> new NBTTagIntArray();
            case 12 -> new NBTTagLongArray();
            default -> null;
        };
    }
}
