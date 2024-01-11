package shordinger.wrapper.net.minecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @deprecated Forge: {@link net.minecraftforge.client.resource.ISelectiveResourceReloadListener}, which selectively
 * allows
 * individual resource types being reloaded should rather be used where possible.
 */
@Deprecated
@SideOnly(Side.CLIENT)
public interface IResourceManagerReloadListener {

    void onResourceManagerReload(IResourceManager resourceManager);
}
