package shordinger.wrapper.net.minecraft.client.gui;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import shordinger.wrapper.net.minecraft.client.resources.I18n;
import shordinger.wrapper.net.minecraft.client.settings.KeyBinding;
import shordinger.wrapper.net.minecraft.util.text.TextFormatting;

@SideOnly(Side.CLIENT)
public class GuiKeyBindingList extends GuiListExtended {

    private final GuiControls controlsScreen;
    private final Minecraft mc;
    private final GuiListExtended.IGuiListEntry[] listEntries;
    private int maxListLabelWidth;

    public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
        super(mcIn, controls.width + 45, controls.height, 63, controls.height - 32, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;
        KeyBinding[] akeybinding = (KeyBinding[]) ArrayUtils.clone(mcIn.gameSettings.keyBindings);
        this.listEntries = new GuiListExtended.IGuiListEntry[akeybinding.length + KeyBinding.getKeybinds()
            .size()];
        Arrays.sort((Object[]) akeybinding);
        int i = 0;
        String s = null;

        for (KeyBinding keybinding : akeybinding) {
            String s1 = keybinding.getKeyCategory();

            if (!s1.equals(s)) {
                s = s1;
                this.listEntries[i++] = new GuiKeyBindingList.CategoryEntry(s1);
            }

            int j = mcIn.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

            if (j > this.maxListLabelWidth) {
                this.maxListLabelWidth = j;
            }

            this.listEntries[i++] = new GuiKeyBindingList.KeyEntry(keybinding);
        }
    }

    protected int getSize() {
        return this.listEntries.length;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries[index];
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 35;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements GuiListExtended.IGuiListEntry {

        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = GuiKeyBindingList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
                              boolean isSelected, float partialTicks) {
            GuiKeyBindingList.this.mc.fontRenderer.drawString(
                this.labelText,
                GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2,
                y + slotHeight - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT - 1,
                16777215);
        }

        /**
         * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
         * clicked and the list should not be dragged.
         */
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX,
                                    int relativeY) {
            return false;
        }

        /**
         * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
         */
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }

        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        }
    }

    @SideOnly(Side.CLIENT)
    public class KeyEntry implements GuiListExtended.IGuiListEntry {

        /**
         * The keybinding specified for this KeyEntry
         */
        private final KeyBinding keybinding;
        /**
         * The localized key description for this KeyEntry
         */
        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;

        private KeyEntry(KeyBinding name) {
            this.keybinding = name;
            this.keyDesc = I18n.format(name.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 95, 20, I18n.format(name.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
                              boolean isSelected, float partialTicks) {
            boolean flag = GuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
            GuiKeyBindingList.this.mc.fontRenderer.drawString(
                this.keyDesc,
                x + 90 - GuiKeyBindingList.this.maxListLabelWidth,
                y + slotHeight / 2 - GuiKeyBindingList.this.mc.fontRenderer.FONT_HEIGHT / 2,
                16777215);
            this.btnReset.x = x + 210;
            this.btnReset.y = y;
            this.btnReset.enabled = !this.keybinding.isSetToDefaultValue();
            this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY, partialTicks);
            this.btnChangeKeyBinding.x = x + 105;
            this.btnChangeKeyBinding.y = y;
            this.btnChangeKeyBinding.displayString = this.keybinding.getDisplayName();
            boolean flag1 = false;
            boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G

            if (this.keybinding.getKeyCode() != 0) {
                for (KeyBinding keybinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if (keybinding != this.keybinding && keybinding.conflicts(this.keybinding)) {
                        flag1 = true;
                        keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(this.keybinding);
                    }
                }
            }

            if (flag) {
                this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> "
                    + TextFormatting.YELLOW
                    + this.btnChangeKeyBinding.displayString
                    + TextFormatting.WHITE
                    + " <";
            } else if (flag1) {
                this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? TextFormatting.GOLD
                    : TextFormatting.RED) + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY, partialTicks);
        }

        /**
         * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
         * clicked and the list should not be dragged.
         */
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX,
                                    int relativeY) {
            if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, mouseX, mouseY)) {
                GuiKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
                return true;
            } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, mouseX, mouseY)) {
                this.keybinding.setToDefault();
                GuiKeyBindingList.this.mc.gameSettings
                    .setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else {
                return false;
            }
        }

        /**
         * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
         */
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
        }

        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
        }
    }
}
