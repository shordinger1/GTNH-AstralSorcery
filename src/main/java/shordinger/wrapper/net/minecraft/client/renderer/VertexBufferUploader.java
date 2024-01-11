package shordinger.wrapper.net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.VertexBuffer;

@SideOnly(Side.CLIENT)
public class VertexBufferUploader extends WorldVertexBufferUploader {

    private VertexBuffer vertexBuffer;

    public void draw(BufferBuilder bufferBuilderIn) {
        bufferBuilderIn.reset();
        this.vertexBuffer.bufferData(bufferBuilderIn.getByteBuffer());
    }

    public void setVertexBuffer(VertexBuffer vertexBufferIn) {
        this.vertexBuffer = vertexBufferIn;
    }
}
