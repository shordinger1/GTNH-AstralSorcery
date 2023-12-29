/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 *  Also Avaliable 1.7.10 source code in https://github.com/shordinger1/GTNH-AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemExplosionResistant
 * Created by HellFirePvP
 * Date: 27.10.2018 / 18:54
 */
public class EntityItemExplosionResistant extends EntityItemHighlighted {

    public EntityItemExplosionResistant(World worldIn) {
        super(worldIn);
    }

    public EntityItemExplosionResistant(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityItemExplosionResistant(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return !source.isExplosion() && super.attackEntityFrom(source, amount);
    }

}
