package com.astralsorcery.gtnh_astralsorcery.lib;

import static com.astralsorcery.gtnh_astralsorcery.common.item.AstralItemList.amulet;
import static com.astralsorcery.gtnh_astralsorcery.lib.TextHandler.texter;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ASCreativeTabs {

    public static final CreativeTabs tabMetaItem01 = new CreativeTabs(texter("itemGroup.AstralSorcery items")) {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return amulet.getItem();
        }
    };

    public static final CreativeTabs tabMetaBlock01 = new CreativeTabs(texter("itemGroup.AstralSorcery Blocks")) {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return amulet.getItem();
        }
    };
}
