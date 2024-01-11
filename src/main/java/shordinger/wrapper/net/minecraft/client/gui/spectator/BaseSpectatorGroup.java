package shordinger.wrapper.net.minecraft.client.gui.spectator;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.client.gui.spectator.categories.TeleportToPlayer;
import shordinger.wrapper.net.minecraft.client.gui.spectator.categories.TeleportToTeam;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

@SideOnly(Side.CLIENT)
public class BaseSpectatorGroup implements ISpectatorMenuView {

    private final List<ISpectatorMenuObject> items = Lists.<ISpectatorMenuObject>newArrayList();

    public BaseSpectatorGroup() {
        this.items.add(new TeleportToPlayer());
        this.items.add(new TeleportToTeam());
    }

    public List<ISpectatorMenuObject> getItems() {
        return this.items;
    }

    public ITextComponent getPrompt() {
        return new TextComponentTranslation("spectatorMenu.root.prompt", new Object[0]);
    }
}
