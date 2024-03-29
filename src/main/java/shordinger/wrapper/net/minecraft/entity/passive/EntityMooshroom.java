package shordinger.wrapper.net.minecraft.entity.passive;

import javax.annotation.Nullable;

import shordinger.wrapper.net.minecraft.entity.EntityAgeable;
import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.item.EntityItem;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.init.Blocks;
import shordinger.wrapper.net.minecraft.init.Items;
import shordinger.wrapper.net.minecraft.init.SoundEvents;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import shordinger.wrapper.net.minecraft.util.EnumHand;
import shordinger.wrapper.net.minecraft.util.EnumParticleTypes;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;
import shordinger.wrapper.net.minecraft.util.datafix.DataFixer;
import shordinger.wrapper.net.minecraft.world.World;
import shordinger.wrapper.net.minecraft.world.storage.loot.LootTableList;

public class EntityMooshroom extends EntityCow implements net.minecraftforge.common.IShearable {

    public EntityMooshroom(World worldIn) {
        super(worldIn);
        this.setSize(0.9F, 1.4F);
        this.spawnableBlock = Blocks.MYCELIUM;
    }

    public static void registerFixesMooshroom(DataFixer fixer) {
        EntityLiving.registerFixesMob(fixer, EntityMooshroom.class);
    }

    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack.getItem() == Items.BOWL && this.getGrowingAge() >= 0 && !player.capabilities.isCreativeMode) {
            itemstack.shrink(1);

            if (itemstack.isEmpty()) {
                player.setHeldItem(hand, new ItemStack(Items.MUSHROOM_STEW));
            } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.MUSHROOM_STEW))) {
                player.dropItem(new ItemStack(Items.MUSHROOM_STEW), false);
            }

            return true;
        } else if (false && itemstack.getItem() == Items.SHEARS && this.getGrowingAge() >= 0) // Forge Disable, Moved to
        // onSheared
        {
            this.setDead();
            this.world.spawnParticle(
                EnumParticleTypes.EXPLOSION_LARGE,
                this.posX,
                this.posY + (double) (this.height / 2.0F),
                this.posZ,
                0.0D,
                0.0D,
                0.0D);

            if (!this.world.isRemote) {
                EntityCow entitycow = new EntityCow(this.world);
                entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                entitycow.setHealth(this.getHealth());
                entitycow.renderYawOffset = this.renderYawOffset;

                if (this.hasCustomName()) {
                    entitycow.setCustomNameTag(this.getCustomNameTag());
                }

                this.world.spawnEntity(entitycow);

                for (int i = 0; i < 5; ++i) {
                    this.world.spawnEntity(
                        new EntityItem(
                            this.world,
                            this.posX,
                            this.posY + (double) this.height,
                            this.posZ,
                            new ItemStack(Blocks.RED_MUSHROOM)));
                }

                itemstack.damageItem(1, player);
                this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.processInteract(player, hand);
        }
    }

    public EntityMooshroom createChild(EntityAgeable ageable) {
        return new EntityMooshroom(this.world);
    }

    @Override
    public boolean isShearable(ItemStack item, net.minecraft.world.IBlockAccess world,
                               net.minecraft.util.math.BlockPos pos) {
        return getGrowingAge() >= 0;
    }

    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world,
                                               net.minecraft.util.math.BlockPos pos, int fortune) {
        this.setDead();
        ((net.minecraft.world.WorldServer) this.world).spawnParticle(
            EnumParticleTypes.EXPLOSION_LARGE,
            false,
            this.posX,
            this.posY + (double) (this.height / 2.0F),
            this.posZ,
            1,
            0.0D,
            0.0D,
            0.0D,
            0.0D);

        EntityCow entitycow = new EntityCow(this.world);
        entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        entitycow.setHealth(this.getHealth());
        entitycow.renderYawOffset = this.renderYawOffset;

        if (this.hasCustomName()) {
            entitycow.setCustomNameTag(this.getCustomNameTag());
        }

        this.world.spawnEntity(entitycow);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int i = 0; i < 5; ++i) {
            ret.add(new ItemStack(Blocks.RED_MUSHROOM));
        }

        this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
        return ret;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.ENTITIES_MUSHROOM_COW;
    }
}
