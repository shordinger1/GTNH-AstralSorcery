package shordinger.wrapper.net.minecraft.client.gui.advancements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum AdvancementState {

    OBTAINED(0),
    UNOBTAINED(1);

    private final int id;

    private AdvancementState(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
