package shordinger.wrapper.net.minecraft.entity.passive;

import shordinger.wrapper.net.minecraft.entity.EntityLiving;
import shordinger.wrapper.net.minecraft.entity.player.EntityPlayer;
import shordinger.wrapper.net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimals {

    public EntityAmbientCreature(World worldIn) {
        super(worldIn);
    }

    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }
}
