package shordinger.wrapper.net.minecraft.client.resources;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public interface IResourceManager {

    Set<String> getResourceDomains();

    IResource getResource(ResourceLocation location) throws IOException;

    /**
     * Gets all versions of the resource identified by {@code location}. The list is ordered by resource pack priority
     * from lowest to highest.
     */
    List<IResource> getAllResources(ResourceLocation location) throws IOException;
}
