package shordinger.wrapper.net.minecraft.client.resources;

import java.io.File;
import java.io.FileNotFoundException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourcePackFileNotFoundException extends FileNotFoundException {

    public ResourcePackFileNotFoundException(File resourcePack, String fileName) {
        super(String.format("'%s' in ResourcePack '%s'", fileName, resourcePack));
    }
}
