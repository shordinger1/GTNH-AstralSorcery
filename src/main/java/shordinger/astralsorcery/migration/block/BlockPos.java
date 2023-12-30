//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shordinger.astralsorcery.migration.block;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraft.util.Vec3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.astralsorcery.common.util.data.WorldBlockPos;
import shordinger.astralsorcery.migration.MathHelper;
import shordinger.astralsorcery.migration.Rotation;

@Immutable
public class BlockPos extends Vec3 {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    private static final int NUM_Z_BITS;
    private static final int NUM_Y_BITS;
    private static final int Y_SHIFT;
    private static final int X_SHIFT;
    private static final long X_MASK;
    private static final long Y_MASK;
    private static final long Z_MASK;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPos(Entity source) {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPos(Vec3 vec) {
        this(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public BlockPos(TileEntity te) {
        this(te.xCoord, te.yCoord, te.zCoord);
    }

    public BlockPos add(double x, double y, double z) {
        return x == 0.0 && y == 0.0 && z == 0.0 ? this
            : new BlockPos((double) xCoord + x, (double) yCoord + y, (double) zCoord + z);
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(xCoord + x, yCoord + y, zCoord + z);
    }

    public int getX() {
        return (int) xCoord;
    }

    public int getY() {
        return (int) yCoord;
    }

    public int getZ() {
        return (int) zCoord;
    }

    // public BlockPos add(BlockPos vec) {
    // return this.add(vec.getX(), vec.getY(), vec.getZ());
    // }
    //
    // public BlockPos subtract(BlockPos vec) {
    // return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
    // }

    public BlockPos up() {
        return this.up(1);
    }

    public BlockPos up(int n) {
        return this.offset(ForgeDirection.UP, n);
    }

    public BlockPos down() {
        return this.down(1);
    }

    public BlockPos down(int n) {
        return this.offset(ForgeDirection.DOWN, n);
    }

    public BlockPos north() {
        return this.north(1);
    }

    public BlockPos north(int n) {
        return this.offset(ForgeDirection.NORTH, n);
    }

    public BlockPos south() {
        return this.south(1);
    }

    public BlockPos south(int n) {
        return this.offset(ForgeDirection.SOUTH, n);
    }

    public BlockPos west() {
        return this.west(1);
    }

    public BlockPos west(int n) {
        return this.offset(ForgeDirection.WEST, n);
    }

    public BlockPos east() {
        return this.east(1);
    }

    public BlockPos east(int n) {
        return this.offset(ForgeDirection.EAST, n);
    }

    public BlockPos offset(ForgeDirection facing) {
        return this.offset(facing, 1);
    }

    public BlockPos offset(ForgeDirection facing, int n) {
        return n == 0 ? this
            : new BlockPos(
            xCoord + facing.getFrontOffsetX() * n,
            yCoord + facing.getFrontOffsetY() * n,
            zCoord + facing.getFrontOffsetZ() * n);
    }

    public BlockPos rotate(Rotation rotationIn) {
        return switch (rotationIn) {
            default -> this;
            case CLOCKWISE_90 -> new BlockPos(-zCoord, yCoord, xCoord);
            case CLOCKWISE_180 -> new BlockPos(-xCoord, yCoord, -zCoord);
            case COUNTERCLOCKWISE_90 -> new BlockPos(zCoord, yCoord, -xCoord);
        };
    }

    public BlockPos crossProduct(Vec3 vec) {
        var newVec = new BlockPos(vec);
        return new BlockPos(
            yCoord * newVec.getZ() - zCoord * newVec.getY(),
            zCoord * newVec.getX() - xCoord * newVec.getZ(),
            xCoord * newVec.getY() - yCoord * newVec.getX());
    }

    public long toLong() {
        return ((long) xCoord & X_MASK) << X_SHIFT | ((long) yCoord & Y_MASK) << Y_SHIFT | ((long) zCoord & Z_MASK);
    }

    public static BlockPos fromLong(long serialized) {
        int i = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(i, j, k);
    }

    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        return getAllInBox(
            Math.min(from.getX(), to.getX()),
            Math.min(from.getY(), to.getY()),
            Math.min(from.getZ(), to.getZ()),
            Math.max(from.getX(), to.getX()),
            Math.max(from.getY(), to.getY()),
            Math.max(from.getZ(), to.getZ()));
    }

    public static Iterable<BlockPos> getAllInBox(final int x1, final int y1, final int z1, final int x2, final int y2,
                                                 final int z2) {
        return new Iterable<BlockPos>() {

            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {

                    private boolean first = true;
                    private int lastPosX;
                    private int lastPosY;
                    private int lastPosZ;

                    protected BlockPos computeNext() {
                        if (this.first) {
                            this.first = false;
                            this.lastPosX = x1;
                            this.lastPosY = y1;
                            this.lastPosZ = z1;
                            return new BlockPos(x1, y1, z1);
                        } else if (this.lastPosX == x2 && this.lastPosY == y2 && this.lastPosZ == z2) {
                            return (BlockPos) this.endOfData();
                        } else {
                            if (this.lastPosX < x2) {
                                ++this.lastPosX;
                            } else if (this.lastPosY < y2) {
                                this.lastPosX = x1;
                                ++this.lastPosY;
                            } else if (this.lastPosZ < z2) {
                                this.lastPosX = x1;
                                this.lastPosY = y1;
                                ++this.lastPosZ;
                            }

                            return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
                        }
                    }
                };
            }
        };
    }

    public BlockPos toImmutable() {
        return this;
    }

    public static Iterable<MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
        return getAllInBoxMutable(
            Math.min(from.getX(), to.getX()),
            Math.min(from.getY(), to.getY()),
            Math.min(from.getZ(), to.getZ()),
            Math.max(from.getX(), to.getX()),
            Math.max(from.getY(), to.getY()),
            Math.max(from.getZ(), to.getZ()));
    }

    public static Iterable<MutableBlockPos> getAllInBoxMutable(final int x1, final int y1, final int z1, final int x2,
                                                               final int y2, final int z2) {
        return new Iterable<MutableBlockPos>() {

            public Iterator<MutableBlockPos> iterator() {
                return new AbstractIterator<MutableBlockPos>() {

                    private MutableBlockPos pos;

                    protected MutableBlockPos computeNext() {
                        if (this.pos == null) {
                            this.pos = new MutableBlockPos(x1, y1, z1);
                            return this.pos;
                        } else if (this.pos.x == x2 && this.pos.y == y2 && this.pos.z == z2) {
                            return (MutableBlockPos) this.endOfData();
                        } else {
                            if (this.pos.x < x2) {
                                ++this.pos.x;
                            } else if (this.pos.y < y2) {
                                this.pos.x = x1;
                                ++this.pos.y;
                            } else if (this.pos.z < z2) {
                                this.pos.x = x1;
                                this.pos.y = y1;
                                ++this.pos.z;
                            }

                            return this.pos;
                        }
                    }
                };
            }
        };
    }

    static {
        NUM_Z_BITS = NUM_X_BITS;
        NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
        Y_SHIFT = NUM_Z_BITS;
        X_SHIFT = Y_SHIFT + NUM_Y_BITS;
        X_MASK = (1L << NUM_X_BITS) - 1L;
        Y_MASK = (1L << NUM_Y_BITS) - 1L;
        Z_MASK = (1L << NUM_Z_BITS) - 1L;
    }

    public double getDistance(int xIn, int yIn, int zIn) {
        double d0 = (double) (this.getX() - xIn);
        double d1 = (double) (this.getY() - yIn);
        double d2 = (double) (this.getZ() - zIn);
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double distanceSq(double toX, double toY, double toZ) {
        double d0 = (double) this.getX() - toX;
        double d1 = (double) this.getY() - toY;
        double d2 = (double) this.getZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceSqToCenter(double xIn, double yIn, double zIn) {
        double d0 = (double) this.getX() + 0.5 - xIn;
        double d1 = (double) this.getY() + 0.5 - yIn;
        double d2 = (double) this.getZ() + 0.5 - zIn;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceSq(WorldBlockPos pos) {
        return distanceSq(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public static final class PooledMutableBlockPos extends MutableBlockPos {

        private boolean released;
        private static final List<PooledMutableBlockPos> POOL = Lists.newArrayList();

        private PooledMutableBlockPos(int xIn, int yIn, int zIn) {
            super(xIn, yIn, zIn);
        }

        public static PooledMutableBlockPos retain() {
            return retain(0, 0, 0);
        }

        public static PooledMutableBlockPos retain(double xIn, double yIn, double zIn) {
            return retain(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
        }

        @SideOnly(Side.CLIENT)
        public static PooledMutableBlockPos retain(Vec3 vec) {
            var newVec = new BlockPos(vec);
            return retain(newVec.getX(), newVec.getY(), newVec.getZ());
        }

        public static PooledMutableBlockPos retain(int xIn, int yIn, int zIn) {
            synchronized (POOL) {
                if (!POOL.isEmpty()) {
                    PooledMutableBlockPos blockpos$pooledmutableblockpos = POOL.remove(POOL.size() - 1);
                    if (blockpos$pooledmutableblockpos != null && blockpos$pooledmutableblockpos.released) {
                        blockpos$pooledmutableblockpos.released = false;
                        blockpos$pooledmutableblockpos.setPos(xIn, yIn, zIn);
                        return blockpos$pooledmutableblockpos;
                    }
                }
            }

            return new PooledMutableBlockPos(xIn, yIn, zIn);
        }

        public void release() {
            synchronized (POOL) {
                if (POOL.size() < 100) {
                    POOL.add(this);
                }

                this.released = true;
            }
        }

        public PooledMutableBlockPos setPos(int xIn, int yIn, int zIn) {
            if (this.released) {
                BlockPos.LOGGER.error("PooledMutableBlockPosition modified after it was released.", new Throwable());
                this.released = false;
            }

            return (PooledMutableBlockPos) super.setPos(xIn, yIn, zIn);
        }

        @SideOnly(Side.CLIENT)
        public PooledMutableBlockPos setPos(Entity entityIn) {
            return (PooledMutableBlockPos) super.setPos(entityIn);
        }

        public PooledMutableBlockPos setPos(double xIn, double yIn, double zIn) {
            return (PooledMutableBlockPos) super.setPos(xIn, yIn, zIn);
        }

        public PooledMutableBlockPos move(ForgeDirection facing) {
            return (PooledMutableBlockPos) super.move(facing);
        }

        public PooledMutableBlockPos move(ForgeDirection facing, int n) {
            return (PooledMutableBlockPos) super.move(facing, n);
        }
    }

    public static class MutableBlockPos extends BlockPos {

        protected int x;
        protected int y;
        protected int z;

        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public MutableBlockPos(int x_, int y_, int z_) {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        public BlockPos add(double x, double y, double z) {
            return super.add(x, y, z).toImmutable();
        }

        public BlockPos add(int x, int y, int z) {
            return super.add(x, y, z).toImmutable();
        }

        public BlockPos offset(ForgeDirection facing, int n) {
            return super.offset(facing, n).toImmutable();
        }

        public BlockPos rotate(Rotation rotationIn) {
            return super.rotate(rotationIn).toImmutable();
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public MutableBlockPos setPos(int xIn, int yIn, int zIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            return this;
        }

        public MutableBlockPos setPos(double xIn, double yIn, double zIn) {
            return this.setPos(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
        }

        @SideOnly(Side.CLIENT)
        public MutableBlockPos setPos(Entity entityIn) {
            return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
        }

        public MutableBlockPos setPos(Vec3 vec) {
            var newVec = new BlockPos(vec);
            return this.setPos(newVec.getX(), newVec.getY(), newVec.getZ());
        }

        public MutableBlockPos move(ForgeDirection facing) {
            return this.move(facing, 1);
        }

        public MutableBlockPos move(ForgeDirection facing, int n) {
            return this.setPos(
                this.x + facing.getFrontOffsetX() * n,
                this.y + facing.getFrontOffsetY() * n,
                this.z + facing.getFrontOffsetZ() * n);
        }

        public void setY(int yIn) {
            this.y = yIn;
        }

        public BlockPos toImmutable() {
            return new BlockPos(this);
        }
    }
}
