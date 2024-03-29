package shordinger.wrapper.net.minecraft.realms;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.renderer.vertex.VertexFormatElement;

@SideOnly(Side.CLIENT)
public class RealmsVertexFormatElement {

    private final VertexFormatElement v;

    public RealmsVertexFormatElement(VertexFormatElement vIn) {
        this.v = vIn;
    }

    public VertexFormatElement getVertexFormatElement() {
        return this.v;
    }

    public boolean isPosition() {
        return this.v.isPositionElement();
    }

    public int getIndex() {
        return this.v.getIndex();
    }

    public int getByteSize() {
        return this.v.getSize();
    }

    public int getCount() {
        return this.v.getElementCount();
    }

    public int hashCode() {
        return this.v.hashCode();
    }

    public boolean equals(Object p_equals_1_) {
        return this.v.equals(p_equals_1_);
    }

    public String toString() {
        return this.v.toString();
    }
}
