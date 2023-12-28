/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util.data;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

import shordinger.astralsorcery.common.util.ItemComparator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreDictUniqueStackList
 * Created by HellFirePvP
 * Date: 09.04.2017 / 23:30
 */
public class OreDictUniqueStackList extends NonNullList<ItemStack> {

    @Override
    public boolean add(ItemStack stack) {
        return !contains(stack) && super.add(stack);
    }

    @Override
    public boolean contains(Object o) {
        Iterator<ItemStack> it = iterator();
        if (o == null) {
            while (it.hasNext()) {
                if (it.next() == null) {
                    return true;
                }
            }
        } else {
            if (!(o instanceof ItemStack stack)) return false;
            while (it.hasNext()) {
                if (ItemComparator
                    .compare(it.next(), stack, ItemComparator.Clause.ITEM, ItemComparator.Clause.META_STRICT)) {
                    return true;
                }
            }
        }
        return false;
    }
}
