package shordinger.wrapper.net.minecraft.advancements.critereon;

import shordinger.wrapper.net.minecraft.advancements.ICriterionInstance;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

public class AbstractCriterionInstance implements ICriterionInstance {

    private final ResourceLocation criterion;

    public AbstractCriterionInstance(ResourceLocation criterionIn) {
        this.criterion = criterionIn;
    }

    public ResourceLocation getId() {
        return this.criterion;
    }

    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.criterion + '}';
    }
}
