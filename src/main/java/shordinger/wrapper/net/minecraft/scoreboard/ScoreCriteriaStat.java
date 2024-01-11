package shordinger.wrapper.net.minecraft.scoreboard;

import shordinger.wrapper.net.minecraft.stats.StatBase;

public class ScoreCriteriaStat extends ScoreCriteria {

    private final StatBase stat;

    public ScoreCriteriaStat(StatBase statIn) {
        super(statIn.statId);
        this.stat = statIn;
    }
}
