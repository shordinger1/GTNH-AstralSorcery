/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 * Shordinger / GTNH AstralSorcery 2024
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package shordinger.astralsorcery.client.util.obj;

import java.util.ArrayList;

import shordinger.astralsorcery.migration.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * HellFirePvP@Admin
 * Date: 15.06.2015 / 00:10
 * on WingsExMod
 * GroupObject
 */
public class GroupObject {

    public String name;
    public ArrayList<Face> faces = new ArrayList<Face>();
    public int glDrawingMode;

    public GroupObject() {
        this("");
    }

    public GroupObject(String name) {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode) {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render(VertexFormat vf) {
        if (faces.size() > 0) {
            BufferBuilder vb = Tessellator.instance
                .getBuffer();
            vb.begin(glDrawingMode, vf);
            render(vb);
            Tessellator.instance
                .draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder vb) {
        if (faces.size() > 0) {
            for (Face face : faces) {
                face.addFaceForRender(vb);
            }
        }
    }
}
