package shordinger.wrapper.net.minecraft.client.renderer.block.model.multipart;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.block.state.BlockStateContainer;
import shordinger.wrapper.net.minecraft.block.state.IBlockState;

@SideOnly(Side.CLIENT)
public class ConditionAnd implements ICondition {

    private final Iterable<ICondition> conditions;

    public ConditionAnd(Iterable<ICondition> conditionsIn) {
        this.conditions = conditionsIn;
    }

    public Predicate<IBlockState> getPredicate(final BlockStateContainer blockState) {
        return Predicates.and(Iterables.transform(this.conditions, new Function<ICondition, Predicate<IBlockState>>() {

            @Nullable
            public Predicate<IBlockState> apply(@Nullable ICondition p_apply_1_) {
                return p_apply_1_ == null ? null : p_apply_1_.getPredicate(blockState);
            }
        }));
    }
}
