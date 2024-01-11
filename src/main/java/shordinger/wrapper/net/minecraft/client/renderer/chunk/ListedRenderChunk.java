package shordinger.wrapper.net.minecraft.client.renderer.chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.GLAllocation;
import shordinger.wrapper.net.minecraft.client.renderer.RenderGlobal;
import shordinger.wrapper.net.minecraft.util.BlockRenderLayer;
import shordinger.wrapper.net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ListedRenderChunk extends RenderChunk {

    private final int baseDisplayList = GLAllocation.generateDisplayLists(BlockRenderLayer.values().length);

    public ListedRenderChunk(World worldIn, RenderGlobal renderGlobalIn, int index) {
        super(worldIn, renderGlobalIn, index);
    }

    public int getDisplayList(BlockRenderLayer layer, CompiledChunk p_178600_2_) {
        return !p_178600_2_.isLayerEmpty(layer) ? this.baseDisplayList + layer.ordinal() : -1;
    }

    public void deleteGlResources() {
        super.deleteGlResources();
        GLAllocation.deleteDisplayLists(this.baseDisplayList, BlockRenderLayer.values().length);
    }
}
