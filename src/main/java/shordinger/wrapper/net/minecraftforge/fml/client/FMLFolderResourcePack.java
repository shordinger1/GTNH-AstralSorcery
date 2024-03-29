/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package shordinger.wrapper.net.minecraftforge.fml.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import shordinger.wrapper.net.minecraft.client.resources.FolderResourcePack;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLContainerHolder;
import shordinger.wrapper.net.minecraftforge.fml.common.FMLLog;
import shordinger.wrapper.net.minecraftforge.fml.common.ModContainer;

public class FMLFolderResourcePack extends FolderResourcePack implements FMLContainerHolder {

    private ModContainer container;

    public FMLFolderResourcePack(ModContainer container) {
        super(container.getSource());
        this.container = container;
    }

    @Override
    protected boolean hasResourceName(String name) {
        return super.hasResourceName(name);
    }

    @Override
    public String getPackName() {
        return "FMLFileResourcePack:" + container.getName();
    }

    @Override
    protected InputStream getInputStreamByName(String resourceName) throws IOException {
        try {
            return super.getInputStreamByName(resourceName);
        } catch (IOException ioe) {
            if ("pack.mcmeta".equals(resourceName)) {
                FMLLog.log.debug("Mod {} is missing a pack.mcmeta file, substituting a dummy one", container.getName());
                return new ByteArrayInputStream(
                    ("{\n" + " \"pack\": {\n"
                        + "   \"description\": \"dummy FML pack for "
                        + container.getName()
                        + "\",\n"
                        + "   \"pack_format\": 2\n"
                        + "}\n"
                        + "}").getBytes(StandardCharsets.UTF_8));
            } else throw ioe;
        }
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return ImageIO.read(getInputStreamByName(container.getMetadata().logoFile));
    }

    @Override
    public ModContainer getFMLContainer() {
        return container;
    }

}
