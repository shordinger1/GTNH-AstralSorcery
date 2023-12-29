/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.entities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shordinger.astralsorcery.common.item.base.ItemHighlighted;
import shordinger.astralsorcery.migration.EntityData.DataParameter;
import shordinger.astralsorcery.migration.EntityData.DataSerializers;
import shordinger.astralsorcery.migration.EntityData.EntityDataManager;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityItemHighlighted
 * Created by HellFirePvP
 * Date: 13.05.2016 / 13:59
 */
public class EntityItemHighlighted extends EntityItem {

    public EntityDataManager dataManager;
    private static final DataParameter<Integer> DATA_COLOR = EntityDataManager
        .createKey(EntityItemHighlighted.class, DataSerializers.VARINT);

    public EntityItemHighlighted(World worldIn) {
        super(worldIn);
        applyColor(Color.WHITE);
    }

    public EntityItemHighlighted(World worldIn, double x, double y, double z, ItemStack stack) {
        super(worldIn, x, y, z, stack);
        applyColor(
            (!stack.isEmpty() && stack.getItem() instanceof ItemHighlighted)
                ? ((ItemHighlighted) stack.getItem()).getHightlightColor(stack)
                : Color.WHITE);
    }

    public EntityItemHighlighted(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        applyColor(Color.WHITE);
    }

    @Override
    public void setItem(ItemStack stack) {
        super.setItem(stack);

        applyColor(
            (!stack.isEmpty() && stack.getItem() instanceof ItemHighlighted)
                ? ((ItemHighlighted) stack.getItem()).getHightlightColor(stack)
                : Color.WHITE);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(DATA_COLOR, 0);
    }

    public void applyColor(Color color) {
        this.dataManager.set(DATA_COLOR, color.getRGB());
        this.dataManager.setDirty(DATA_COLOR);
    }

    public Color getHighlightColor() {
        int colorInt = this.dataManager.get(DATA_COLOR);
        return new Color(colorInt, false);
    }

}
