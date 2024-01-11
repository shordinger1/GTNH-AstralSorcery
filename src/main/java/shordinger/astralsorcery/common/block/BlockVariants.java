/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.common.block;

import java.util.LinkedList;
import java.util.List;

import shordinger.wrapper.net.minecraft.block.properties.IProperty;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;
import shordinger.wrapper.net.minecraft.util.IStringSerializable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockVariants
 * Created by HellFirePvP
 * Date: 31.07.2016 / 09:30
 */
public interface BlockVariants {

    default <T extends Comparable<T>> List<IBlockState> singleEnumPropertyStates(IBlockState defaultState,
                                                                                 IProperty<T> prop, T[] enumValues) {
        List<IBlockState> ret = new LinkedList<>();
        for (T val : enumValues) {
            ret.add(defaultState.withProperty(prop, val));
        }
        return ret;
    }

    default <T extends Comparable<T> & IStringSerializable> String extractEnumPropertyString(IBlockState state,
                                                                                             IProperty<T> property) {
        return state.getValue(property)
            .getName();
    }

    public List<IBlockState> getValidStates();

    public String getStateName(IBlockState state);

    default public String getBlockName(IBlockState state) {
        return state.getBlock()
            .getClass()
            .getSimpleName();
    }

}
