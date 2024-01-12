package shordinger.wrapper.net.minecraft.client.gui.spectator.categories;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.gui.Gui;
import shordinger.wrapper.net.minecraft.client.gui.GuiSpectator;
import shordinger.wrapper.net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import shordinger.wrapper.net.minecraft.client.gui.spectator.ISpectatorMenuView;
import shordinger.wrapper.net.minecraft.client.gui.spectator.PlayerMenuObject;
import shordinger.wrapper.net.minecraft.client.gui.spectator.SpectatorMenu;
import shordinger.wrapper.net.minecraft.client.network.NetworkPlayerInfo;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;
import shordinger.wrapper.net.minecraft.util.text.TextComponentTranslation;
import shordinger.wrapper.net.minecraft.world.GameType;

@SideOnly(Side.CLIENT)
public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject {

    private static final Ordering<NetworkPlayerInfo> PROFILE_ORDER = Ordering.from(new Comparator<NetworkPlayerInfo>() {

        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            return ComparisonChain.start()
                .compare(
                    p_compare_1_.getGameProfile()
                        .getId(),
                    p_compare_2_.getGameProfile()
                        .getId())
                .result();
        }
    });
    private final List<ISpectatorMenuObject> items;

    public TeleportToPlayer() {
        this(
            PROFILE_ORDER.sortedCopy(
                Minecraft.getMinecraft()
                    .getConnection()
                    .getPlayerInfoMap()));
    }

    public TeleportToPlayer(Collection<NetworkPlayerInfo> profiles) {
        this.items = Lists.<ISpectatorMenuObject>newArrayList();

        for (NetworkPlayerInfo networkplayerinfo : PROFILE_ORDER.sortedCopy(profiles)) {
            if (networkplayerinfo.getGameType() != GameType.SPECTATOR) {
                this.items.add(new PlayerMenuObject(networkplayerinfo.getGameProfile()));
            }
        }
    }

    public List<ISpectatorMenuObject> getItems() {
        return this.items;
    }

    public ITextComponent getPrompt() {
        return new TextComponentTranslation("spectatorMenu.teleport.prompt", new Object[0]);
    }

    public void selectItem(SpectatorMenu menu) {
        menu.selectCategory(this);
    }

    public ITextComponent getSpectatorName() {
        return new TextComponentTranslation("spectatorMenu.teleport", new Object[0]);
    }

    public void renderIcon(float brightness, int alpha) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
    }

    public boolean isEnabled() {
        return !this.items.isEmpty();
    }
}
