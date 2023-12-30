//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package shordinger.astralsorcery.migration;

import net.minecraftforge.common.util.ForgeDirection;

public enum Rotation {

    NONE("rotate_0"),
    CLOCKWISE_90("rotate_90"),
    CLOCKWISE_180("rotate_180"),
    COUNTERCLOCKWISE_90("rotate_270");

    private final String name;
    private static final String[] rotationNames = new String[values().length];

    private Rotation(String nameIn) {
        this.name = nameIn;
    }

    public Rotation add(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return switch (this) {
                    case NONE -> CLOCKWISE_180;
                    case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
                    case CLOCKWISE_180 -> NONE;
                    case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
                };
            }
            case COUNTERCLOCKWISE_90 -> {
                return switch (this) {
                    case NONE -> COUNTERCLOCKWISE_90;
                    case CLOCKWISE_90 -> NONE;
                    case CLOCKWISE_180 -> CLOCKWISE_90;
                    case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
                };
            }
            case CLOCKWISE_90 -> {
                return switch (this) {
                    case NONE -> CLOCKWISE_90;
                    case CLOCKWISE_90 -> CLOCKWISE_180;
                    case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
                    case COUNTERCLOCKWISE_90 -> NONE;
                };
            }
            default -> {
                return this;
            }
        }
    }

    public ForgeDirection rotate(ForgeDirection facing) {
        if (facing.getAxis() == Axis.Y) {
            return facing;
        } else {
            return switch (this) {
                case CLOCKWISE_90 -> facing.rotateY();
                case CLOCKWISE_180 -> facing.getOpposite();
                case COUNTERCLOCKWISE_90 -> facing.rotateYCCW();
                default -> facing;
            };
        }
    }

    public int rotate(int p_185833_1_, int p_185833_2_) {
        return switch (this) {
            case CLOCKWISE_90 -> (p_185833_1_ + p_185833_2_ / 4) % p_185833_2_;
            case CLOCKWISE_180 -> (p_185833_1_ + p_185833_2_ / 2) % p_185833_2_;
            case COUNTERCLOCKWISE_90 -> (p_185833_1_ + p_185833_2_ * 3 / 4) % p_185833_2_;
            default -> p_185833_1_;
        };
    }

    static {
        int i = 0;
        Rotation[] var1 = values();
        for (Rotation rotation : var1) {
            rotationNames[i++] = rotation.name;
        }

    }
}
