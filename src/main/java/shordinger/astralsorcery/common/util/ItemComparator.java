/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.util;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

import net.minecraftforge.oredict.OreDictionary;
import shordinger.astralsorcery.common.util.nbt.NBTComparator;
import shordinger.wrapper.net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import shordinger.wrapper.net.minecraftforge.common.util.Constants;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemComparator
 * Created by HellFirePvP
 * Date: 28.03.2019 / 19:16
 */
// Yes, i know, this isn't really a java.lang.Comparator
public class ItemComparator {

    public static boolean compare(@Nonnull ItemStack thisStack, @Nonnull ItemStack sampleCompare, Clause... clauses) {
        Set<Clause> lClauses = Sets.newHashSet(clauses);

        if (lClauses.contains(Clause.ITEM)) {
            if (thisStack.isEmpty() && !sampleCompare.isEmpty()) {
                return false;
            }
            if (!thisStack.isEmpty() && !thisStack.getItem()
                .equals(sampleCompare.getItem())) { // Includes inverse case of the above.
                return false;
            }
        }

        if (lClauses.contains(Clause.AMOUNT_EXACT)) {
            if (thisStack.getCount() != sampleCompare.getCount()) {
                return false;
            }
        } else if (lClauses.contains(Clause.AMOUNT_LEAST)) {
            if (thisStack.getCount() > sampleCompare.getCount()) {
                return false;
            }
        }

        if (lClauses.contains(Clause.META_STRICT)) {
            if (thisStack.getMetadata() != sampleCompare.getMetadata()) {
                return false;
            }
        } else if (lClauses.contains(Clause.META_WILDCARD)) {
            if (thisStack.getMetadata() != sampleCompare.getMetadata()
                && thisStack.getMetadata() != OreDictionary.WILDCARD_VALUE
                && sampleCompare.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
                return false;
            }
        }

        boolean thisHasTag = thisStack.hasTagCompound() && !isTagEmpty(thisStack.getTagCompound());
        boolean sampleHasTag = sampleCompare.hasTagCompound() && !isTagEmpty(sampleCompare.getTagCompound());

        if (lClauses.contains(Clause.NBT_STRICT)) {
            if (!thisHasTag && sampleHasTag) {
                return false;
            } else if (thisHasTag && (!sampleHasTag || !thisStack.getTagCompound()
                .equals(sampleCompare.getTagCompound()))) {
                return false;
            }
        } else if (lClauses.contains(Clause.NBT_LEAST)) {
            if (thisHasTag) {
                if (!sampleHasTag) {
                    return false;
                }

                if (!NBTComparator.contains(thisStack.getTagCompound(), sampleCompare.getTagCompound())) {
                    return false;
                }
            }
        }

        if (lClauses.contains(Clause.CAPABILITIES_COMPATIBLE)) {
            return thisStack.areCapsCompatible(sampleCompare);
        }

        return true;
    }

    private static boolean isTagEmpty(NBTTagCompound compound) {
        for (Object key : compound.func_150296_c()) {
            NBTBase value = compound.getTag((String) key);
            if (value instanceof NBTTagCompound) {
                if (!isTagEmpty((NBTTagCompound) value)) {
                    return false;
                }
            } else if (value instanceof NBTTagList) {
                if (!isListEmpty((NBTTagList) value)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private static boolean isListEmpty(NBTTagList list) {
        if (list.tagCount() != 0) {
//            if (list.getTagType() != Constants.NBT.TAG_LIST && list.getTagType() != Constants.NBT.TAG_COMPOUND) {
//                return false;
//            }
            return list.func_150306_c(0).length > 0;
        }
        return true;
    }

    public static enum Clause {

        ITEM,

        AMOUNT_EXACT,
        AMOUNT_LEAST,

        META_STRICT,
        META_WILDCARD,

        NBT_STRICT,
        NBT_LEAST,

        CAPABILITIES_COMPATIBLE;

        public static class Sets {

            public static final Clause[] ITEMSTACK_STRICT = {Clause.ITEM, Clause.AMOUNT_EXACT, META_STRICT, NBT_STRICT,
                CAPABILITIES_COMPATIBLE};

        }

    }

}
