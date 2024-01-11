package shordinger.wrapper.net.minecraft.client.tutorial;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompletedTutorialStep implements ITutorialStep {

    private final Tutorial tutorial;

    public CompletedTutorialStep(Tutorial tutorial) {
        this.tutorial = tutorial;
    }
}
