package shordinger.wrapper.net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITabCompleter {

    /**
     * Sets the list of tab completions, as long as they were previously requested.
     */
    void setCompletions(String... newCompletions);
}
