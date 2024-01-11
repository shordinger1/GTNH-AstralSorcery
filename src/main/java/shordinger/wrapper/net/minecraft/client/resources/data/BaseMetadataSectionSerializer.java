package shordinger.wrapper.net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BaseMetadataSectionSerializer<T extends IMetadataSection>
    implements IMetadataSectionSerializer<T> {
}
