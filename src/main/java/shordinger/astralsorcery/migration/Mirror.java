package shordinger.astralsorcery.migration;

import net.minecraftforge.common.util.ForgeDirection;

public enum Mirror {
    NONE("no_mirror"),
    LEFT_RIGHT("mirror_left_right"),
    FRONT_BACK("mirror_front_back");

    private final String name;
    private static final String[] mirrorNames = new String[values().length];

    private Mirror(String nameIn) {
        this.name = nameIn;
    }

    public int mirrorRotation(int rotationIn, int rotationCount) {
        int i = rotationCount / 2;
        int j = rotationIn > i ? rotationIn - rotationCount : rotationIn;
        return switch (this) {
            case FRONT_BACK -> (rotationCount - j) % rotationCount;
            case LEFT_RIGHT -> (i - j + rotationCount) % rotationCount;
            default -> rotationIn;
        };
    }

//    public Rotation toRotation(ForgeDirection facing) {
//        ForgeDirection.Axis enumfacing$axis = facing.getAxis();
//        return this == LEFT_RIGHT && enumfacing$axis == Axis.Z || this == FRONT_BACK && enumfacing$axis == Axis.X ? Rotation.CLOCKWISE_180 : Rotation.NONE;
//    }

    public ForgeDirection mirror(ForgeDirection facing) {
        switch (this) {
            case FRONT_BACK -> {
                if (facing == ForgeDirection.WEST) {
                    return ForgeDirection.EAST;
                } else {
                    if (facing == ForgeDirection.EAST) {
                        return ForgeDirection.WEST;
                    }

                    return facing;
                }
            }
            case LEFT_RIGHT -> {
                if (facing == ForgeDirection.NORTH) {
                    return ForgeDirection.SOUTH;
                } else {
                    if (facing == ForgeDirection.SOUTH) {
                        return ForgeDirection.NORTH;
                    }

                    return facing;
                }
            }
            default -> {
                return facing;
            }
        }
    }

    static {
        int i = 0;
        Mirror[] var1 = values();
        int var2 = var1.length;

        for (Mirror mirror : var1) {
            mirrorNames[i++] = mirror.name;
        }

    }
}
