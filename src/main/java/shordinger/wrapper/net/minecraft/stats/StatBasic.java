package shordinger.wrapper.net.minecraft.stats;

import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

public class StatBasic extends StatBase {

    public StatBasic(String statIdIn, ITextComponent statNameIn, IStatType typeIn) {
        super(statIdIn, statNameIn, typeIn);
    }

    public StatBasic(String statIdIn, ITextComponent statNameIn) {
        super(statIdIn, statNameIn);
    }

    /**
     * Register the stat into StatList.
     */
    public StatBase registerStat() {
        super.registerStat();
        StatList.BASIC_STATS.add(this);
        return this;
    }
}
