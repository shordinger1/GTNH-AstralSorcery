package shordinger.wrapper.net.minecraft.client.renderer.chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.RenderGlobal;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public interface IRenderChunkFactory {

    RenderChunk create(World worldIn, RenderGlobal renderGlobalIn, int index);
}
