package shordinger.wrapper.net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.chunk.ListedRenderChunk;
import shordinger.wrapper.net.minecraft.client.renderer.chunk.RenderChunk;
import shordinger.wrapper.net.minecraft.util.BlockRenderLayer;

@SideOnly(Side.CLIENT)
public class RenderList extends ChunkRenderContainer {

    public void renderChunkLayer(BlockRenderLayer layer) {
        if (this.initialized) {
            for (RenderChunk renderchunk : this.renderChunks) {
                ListedRenderChunk listedrenderchunk = (ListedRenderChunk) renderchunk;
                GlStateManager.pushMatrix();
                this.preRenderChunk(renderchunk);
                GlStateManager.callList(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
                GlStateManager.popMatrix();
            }

            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }
}
