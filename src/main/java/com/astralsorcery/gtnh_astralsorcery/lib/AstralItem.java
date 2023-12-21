package com.astralsorcery.gtnh_astralsorcery.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AstralItem extends Item {

    private final List<String> tooltips = new ArrayList<>();

    private String unlocalizedName;

    public AstralItem(String Name, String MetaName, CreativeTabs aCreativeTabs/* , String aIconPath */) {
        super();
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        MetaName = MetaName.replace(" ", "_");
        this.setCreativeTab(aCreativeTabs);
        this.unlocalizedName = MetaName;
        TextHandler.texter(MetaName + ".name");
        GameRegistry.registerItem(this, MetaName);
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

    @Override
    public Item setUnlocalizedName(String aUnlocalizedName) {
        this.unlocalizedName = aUnlocalizedName;
        return this;
    }

    //
    //
    @Override
    public String getUnlocalizedName(ItemStack aItemStack) {
        return this.unlocalizedName;
    }

    @Override
    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.itemIcon = iconRegister.registerIcon("astralsorcery:" + getUnlocalizedName().replace(' ', '_'));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMetaData) {
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List aList) {
        aList.add(new ItemStack(aItem, 1, 0));
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked" })
    public void addInformation(ItemStack aItemStack, EntityPlayer aEntityPlayer, List aTooltipsList,
        boolean p_77624_4_) {
        if (tooltips.size() > 0) {
            aTooltipsList.addAll(tooltips);
        }
    }

}
