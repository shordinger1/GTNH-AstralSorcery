package shordinger.wrapper.net.minecraft.server.integrated;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.command.ServerCommandManager;

@SideOnly(Side.CLIENT)
public class IntegratedServerCommandManager extends ServerCommandManager {

    public IntegratedServerCommandManager(IntegratedServer server) {
        super(server);
    }
}
