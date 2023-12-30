package shordinger.astralsorcery.migration;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import shordinger.astralsorcery.migration.block.BlockPos;

public class RayTraceResult {
    /**
     * Used to determine what sub-segment is hit
     */
    public int subHit = -1;

    /**
     * Used to add extra hit info
     */
    public Object hitInfo = null;

    private BlockPos blockPos;

    public RayTraceResult.Type typeOfHit;
    public EnumFacing sideHit;
    /**
     * The vector position of the hit
     */
    public BlockPos hitVec;
    /**
     * The hit entity
     */
    public Entity entityHit;

    public RayTraceResult(BlockPos hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
    }

    public RayTraceResult(BlockPos hitVecIn, EnumFacing sideHitIn) {
        this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
    }

    public RayTraceResult(Entity entityIn) {
        this(entityIn, new BlockPos(entityIn.posX, entityIn.posY, entityIn.posZ));
    }

    public RayTraceResult(RayTraceResult.Type typeIn, BlockPos hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
        this.typeOfHit = typeIn;
        this.blockPos = blockPosIn;
        this.sideHit = sideHitIn;
        this.hitVec = new BlockPos(hitVecIn.getX(), hitVecIn.getY(), hitVecIn.getZ());
    }

    public RayTraceResult(Entity entityHitIn, BlockPos hitVecIn) {
        this.typeOfHit = RayTraceResult.Type.ENTITY;
        this.entityHit = entityHitIn;
        this.hitVec = hitVecIn;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public String toString() {
        return "HitResult{type=" + this.typeOfHit + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entityHit + '}';
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY;
    }
}
