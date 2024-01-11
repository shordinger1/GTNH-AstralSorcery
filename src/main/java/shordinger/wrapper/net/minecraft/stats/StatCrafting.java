package shordinger.wrapper.net.minecraft.stats;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import shordinger.wrapper.net.minecraft.item.Item;
import shordinger.wrapper.net.minecraft.util.text.ITextComponent;

public class StatCrafting extends StatBase {

    private final Item item;

    public StatCrafting(String p_i45910_1_, String p_i45910_2_, ITextComponent statNameIn, Item p_i45910_4_) {
        super(p_i45910_1_ + p_i45910_2_, statNameIn);
        this.item = p_i45910_4_;
    }

    @SideOnly(Side.CLIENT)
    public Item getItem() {
        return this.item;
    }
}
