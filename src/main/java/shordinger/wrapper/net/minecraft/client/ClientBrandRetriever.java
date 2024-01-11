package shordinger.wrapper.net.minecraft.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientBrandRetriever {

    public static String getClientModName() {
        return net.minecraftforge.fml.common.FMLCommonHandler.instance()
            .getModName();
    }
}
