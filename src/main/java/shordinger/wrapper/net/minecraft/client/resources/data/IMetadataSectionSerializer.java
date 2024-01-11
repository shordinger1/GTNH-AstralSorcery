package shordinger.wrapper.net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IMetadataSectionSerializer<T extends IMetadataSection> extends JsonDeserializer<T> {

    /**
     * The name of this section type as it appears in JSON.
     */
    String getSectionName();
}
