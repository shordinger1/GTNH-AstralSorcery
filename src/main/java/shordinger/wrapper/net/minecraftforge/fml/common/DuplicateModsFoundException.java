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

package shordinger.wrapper.net.minecraftforge.fml.common;

import java.io.File;
import java.util.Map.Entry;

import com.google.common.collect.SetMultimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.gui.GuiScreen;
import shordinger.wrapper.net.minecraftforge.fml.client.GuiDupesFound;
import shordinger.wrapper.net.minecraftforge.fml.client.IDisplayableError;
import shordinger.wrapper.net.minecraftforge.fml.common.EnhancedRuntimeException.WrappedPrintStream;

public class DuplicateModsFoundException extends LoaderException implements IDisplayableError {

    private static final long serialVersionUID = 1L;
    public SetMultimap<ModContainer, File> dupes;

    public DuplicateModsFoundException(SetMultimap<ModContainer, File> dupes) {
        this.dupes = dupes;
    }

    @Override
    protected void printStackTrace(WrappedPrintStream stream) {
        stream.println("Duplicate Mods:");
        for (Entry<ModContainer, File> e : dupes.entries()) {
            stream.println(
                String.format(
                    "\t%s : %s",
                    e.getKey()
                        .getModId(),
                    e.getValue()
                        .getAbsolutePath()));
        }
        stream.println("");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen createGui() {
        return new GuiDupesFound(this);
    }
}
