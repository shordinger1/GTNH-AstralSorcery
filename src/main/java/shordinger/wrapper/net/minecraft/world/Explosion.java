package shordinger.wrapper.net.minecraft.world;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.Block;
import shordinger.wrapper.net.minecraft.block.material.Material;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.enchantment.EnchantmentProtection;
import shordinger.wrapper.net.minecraft.entity.Entity;
import shordinger.wrapper.net.minecraft.entity.EntityLivingBase;
import shordinger.wrapper.net.minecraft.entity.item.EntityTNTPrimed;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.util.DamageSource;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.SoundCategory;
import shordinger.wrapper.net.minecraft.util.math.AxisAlignedBB;
import shordinger.wrapper.net.minecraft.util.math.BlockPos;
import shordinger.wrapper.net.minecraft.util.math.MathHelper;
import shordinger.wrapper.net.minecraft.util.math.Vec3d;

public class Explosion extends net.minecraft.world.Explosion {

    //    /**
//     * whether or not the explosion sets fire to blocks around it
//     */
    public final boolean causesFire;
//    /**
//     * whether or not this explosion spawns smoke particles
//     */
//    public final boolean damagesTerrain;
//    public final Random random;
//    public final World world;
//    public final double x;
//    public final double y;
//    public final double z;
//    public final Entity exploder;
//    public final float size;
//    /**
//     * A list of ChunkPositions of blocks affected by this explosion
//     */
//    private final List<BlockPos> affectedBlockPositions;
//    /**
//     * Maps players to the knockback vector applied by the explosion, to send to the client
//     */
//    private final Map<EntityPlayer, Vec3d> playerKnockbackMap;
//    private final Vec3d position;

    @SideOnly(Side.CLIENT)
    public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size,
                     List<BlockPos> affectedPositions) {
        this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
    }

    @SideOnly(Side.CLIENT)
    public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire,
                     boolean damagesTerrain, List<BlockPos> affectedPositions) {
        this(worldIn, entityIn, x, y, z, size, causesFire, damagesTerrain);
        this.affectedBlockPositions.addAll(affectedPositions);
    }

    public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire,
                     boolean damagesTerrain) {
        super(worldIn, entityIn, x, y, z, size);
        this.causesFire = causesFire;
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    public void doExplosionA() {
        super.doExplosionA();

    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void doExplosionB(boolean spawnParticles) {
        super.doExplosionB(spawnParticles);

    }

    public Map<EntityPlayer, Vec3d> getPlayerKnockbackMap() {
        return super.func_77277_b();
    }

    /**
     * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
     */
    @Nullable
    public EntityLivingBase getExplosivePlacedBy() {
        return super.getExplosivePlacedBy();
    }

    public void clearAffectedBlockPositions() {
        this.affectedBlockPositions.clear();
    }

    public List<BlockPos> getAffectedBlockPositions() {
        return this.affectedBlockPositions;
    }

    public Vec3d getPosition() {
        return new Vec3d(explosionX, explosionY, explosionZ);
    }
}
