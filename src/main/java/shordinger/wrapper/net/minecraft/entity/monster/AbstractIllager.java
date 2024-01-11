package shordinger.wrapper.net.minecraft.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.entity.EnumCreatureAttribute;
import shordinger.wrapper.net.minecraft.network.datasync.DataParameter;
import shordinger.wrapper.net.minecraft.network.datasync.DataSerializers;
import shordinger.wrapper.net.minecraft.network.datasync.EntityDataManager;
import shordinger.wrapper.net.minecraft.world.World;

public abstract class AbstractIllager extends EntityMob {

    protected static final DataParameter<Byte> AGGRESSIVE = EntityDataManager
        .<Byte>createKey(AbstractIllager.class, DataSerializers.BYTE);

    public AbstractIllager(World p_i47509_1_) {
        super(p_i47509_1_);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(AGGRESSIVE, Byte.valueOf((byte) 0));
    }

    @SideOnly(Side.CLIENT)
    protected boolean isAggressive(int mask) {
        int i = ((Byte) this.dataManager.get(AGGRESSIVE)).byteValue();
        return (i & mask) != 0;
    }

    protected void setAggressive(int mask, boolean value) {
        int i = ((Byte) this.dataManager.get(AGGRESSIVE)).byteValue();

        if (value) {
            i = i | mask;
        } else {
            i = i & ~mask;
        }

        this.dataManager.set(AGGRESSIVE, Byte.valueOf((byte) (i & 255)));
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ILLAGER;
    }

    @SideOnly(Side.CLIENT)
    public AbstractIllager.IllagerArmPose getArmPose() {
        return AbstractIllager.IllagerArmPose.CROSSED;
    }

    @SideOnly(Side.CLIENT)
    public static enum IllagerArmPose {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW;
    }
}
